package org.sharpler.hprofcrawler;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.backend.Index;
import org.sharpler.hprofcrawler.backend.RocksDbBuilder;
import org.sharpler.hprofcrawler.backend.RocksDbStorage;
import org.sharpler.hprofcrawler.dbs.ClassInfoDb;
import org.sharpler.hprofcrawler.dbs.Database;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.inspection.BuildInInspection;
import org.sharpler.hprofcrawler.parser.HprofParser;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public final class Main implements Callable<Integer> {
    static {
        RocksDB.loadLibrary();
    }

    @CommandLine.Option(
            names = {"-f", "--file"},
            paramLabel = "HPROF",
            description = "the hprof file",
            required = true
    )
    private Path dumpPath = Paths.get(".");

    @CommandLine.Option(
            names = {"-i", "--inspection"},
            paramLabel = "BUILD-IN INSPECTION",
            description = "The build-in inspection: ${COMPLETION-CANDIDATES}"
    )
    private BuildInInspection inspection = BuildInInspection.CONSTANT_FIELDS;

    @CommandLine.Option(
            names = {"-b", "--rebuild"},
            paramLabel = "FORCE REBUILD INDEX",
            description = "Force rebuild index"
    )
    private boolean forceRebuild = false;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (!Files.exists(dumpPath)) {
            throw new IllegalArgumentException("File doesn't exist: " + dumpPath);
        }
        var dbsDir = dumpPath.getParent().resolve("dbs");
        boolean shouldRebuild;
        if (Files.exists(dbsDir)) {
            shouldRebuild = forceRebuild;
        } else {
            Files.createDirectory(dbsDir);
            shouldRebuild = true;
        }

        var object2ClassDescriptor = new ColumnFamilyDescriptor("object2Class".getBytes());
        var instancesDescriptor = new ColumnFamilyDescriptor("instances".getBytes());
        var primArraysDescriptor = new ColumnFamilyDescriptor("prim_arrays".getBytes());
        var objectArraysDescriptor = new ColumnFamilyDescriptor("object_arrays".getBytes());
        var namesDescriptor = new ColumnFamilyDescriptor("names".getBytes());
        var classesDescriptor = new ColumnFamilyDescriptor("classes".getBytes());

        try (
                var dbOptions = new Options().setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
                var rocksDb = RocksDB.open(dbOptions, dbsDir.toString());
                var object2ClassHandle = rocksDb.createColumnFamily(object2ClassDescriptor);
                var instancesHandle = rocksDb.createColumnFamily(instancesDescriptor);
                var primArraysHandle = rocksDb.createColumnFamily(primArraysDescriptor);
                var objectArraysHandle = rocksDb.createColumnFamily(objectArraysDescriptor);
                var namesHandle = rocksDb.createColumnFamily(namesDescriptor);
                var classesHandle = rocksDb.createColumnFamily(classesDescriptor);
        ) {
            withDb(
                    shouldRebuild,
                    rocksDb,
                    object2ClassHandle,
                    instancesHandle,
                    primArraysHandle,
                    objectArraysHandle,
                    namesHandle,
                    classesHandle
            );
        }

        return 0;
    }

    private void withDb(
            boolean shouldRebuild,
            RocksDB rocksDB,
            ColumnFamilyHandle object2ClassHandle,
            ColumnFamilyHandle instancesHandle,
            ColumnFamilyHandle primArraysHandle,
            ColumnFamilyHandle objectArraysHandle,
            ColumnFamilyHandle namesHandle,
            ColumnFamilyHandle classesHandle
    ) throws IOException {
        var object2Class = new Object2ClassDb(rocksDB, object2ClassHandle);
        var instances = new InstancesDb(rocksDB, instancesHandle);
        var primArrays = new PrimArraysDb(rocksDB, primArraysHandle);
        var objectArrays = new ObjectArraysDb(rocksDB, objectArraysHandle);
        var names = new NamesDb(rocksDB, namesHandle);
        var classes = new ClassInfoDb(rocksDB, classesHandle);

        var backend = shouldRebuild ?
                initBackend(object2Class, instances, primArrays, objectArrays, names, classes) :
                reloadBackend(object2Class, instances, primArrays, objectArrays, names, classes);

        long startTime = System.currentTimeMillis();
        var result = inspection.run(backend, new TerminalProgress());
        long finishTime = System.currentTimeMillis();

        System.out.printf("Scan time: %d(ms)%n", finishTime - startTime);
        System.out.println(result);
    }

    private static Backend reloadBackend(
            Object2ClassDb object2Class,
            InstancesDb instances,
            PrimArraysDb primArrays,
            ObjectArraysDb objectArrays,
            NamesDb names,
            ClassInfoDb classes
    ) {
        return new Backend(
                new RocksDbStorage(
                        object2Class,
                        instances,
                        primArrays,
                        objectArrays,
                        names,
                        classes
                ),
                Index.reload(primArrays, objectArrays)
        );
    }


    private Backend initBackend(
            Object2ClassDb object2Class,
            InstancesDb instances,
            PrimArraysDb primArrays,
            ObjectArraysDb objectArrays,
            NamesDb names,
            ClassInfoDb classes
    ) throws IOException {
        var builder = new RocksDbBuilder(
                object2Class,
                instances,
                primArrays,
                objectArrays,
                names,
                classes
        );

        var parser = new HprofParser(builder);

        var file = dumpPath.toFile();
        long dumpLength = file.length();

        System.out.printf(
                "Going to parse hprof file: dump file path=%s, dump file size=%d(MB), build-in inspection=%s%n",
                dumpPath, dumpLength / 1024 / 1024, inspection.name()
        );

        long startTime = System.currentTimeMillis();
        parser.parse(file);
        long finishTime = System.currentTimeMillis();

        System.out.printf(
                "time=%s(ms),size=%s(MB),speed=%s(MB/s)%n",
                finishTime - startTime,
                dumpLength / 1024L / 1024L,
                ((double) dumpLength / (finishTime - startTime)) * (1000.0 / 1024.0 / 1024.0)
        );

        startTime = System.currentTimeMillis();
        var backend = builder.build();
        finishTime = System.currentTimeMillis();

        System.out.printf("Build backend: %d(ms)%n", finishTime - startTime);

        startTime = System.currentTimeMillis();
        Database.compactAll(
                new TerminalProgress(),
                object2Class,
                instances,
                primArrays,
                objectArrays,
                names,
                classes
        );
        finishTime = System.currentTimeMillis();

        System.out.printf("compact %d(ms)%n", finishTime - startTime);

        return backend;
    }
}
