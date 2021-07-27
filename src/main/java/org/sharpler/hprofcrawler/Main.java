package org.sharpler.hprofcrawler;

import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.backend.Index;
import org.sharpler.hprofcrawler.backend.LevelDbBuilder;
import org.sharpler.hprofcrawler.backend.LevelDbStorage;
import org.sharpler.hprofcrawler.dbs.ClassInfoDb;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.inspection.BuildInInspection;
import org.sharpler.hprofcrawler.parser.HprofParser;
import org.sharpler.hprofcrawler.views.TerminalProgress;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public final class Main implements Callable<Integer> {
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
        if (!Files.exists(dbsDir)) {
            Files.createDirectory(dbsDir);
            shouldRebuild = true;
        } else {
            shouldRebuild = forceRebuild;
        }


        var backend = shouldRebuild ? initBackend(dbsDir, dumpPath) : reloadBackend(dbsDir);

        long startTime = System.currentTimeMillis();
        var result = inspection.run(backend, new TerminalProgress());
        long finishTime = System.currentTimeMillis();

        System.out.printf("Scan time: %d(ms)%n", finishTime - startTime);

        System.out.println(result);

        return 0;
    }

    Backend initBackend(Path dbsDir, Path dumpPath) throws IOException {
        var builder = LevelDbBuilder.of(dbsDir);

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
        var backend = builder.build(new TerminalProgress());
        finishTime = System.currentTimeMillis();

        System.out.printf("Build backend: %d(ms)%n", finishTime - startTime);

        return backend;
    }

    Backend reloadBackend(Path dir) {
        return Utils.resourceOwner(
                (object2Class, instances, primArraysDb, objectArraysDb, namesDb, classes) -> {
                    var index = Index.reload(primArraysDb, objectArraysDb);
                    return new Backend(
                            new LevelDbStorage(
                                    index,
                                    object2Class,
                                    instances,
                                    primArraysDb,
                                    objectArraysDb,
                                    namesDb,
                                    classes
                            ),
                            index
                    );
                },
                () -> new Object2ClassDb(Utils.openDb(dir.resolve("object2Class"))),
                () -> new InstancesDb(Utils.openDb(dir.resolve("instances"))),
                () -> new PrimArraysDb(Utils.openDb(dir.resolve("prim_arrays"))),
                () -> new ObjectArraysDb(Utils.openDb(dir.resolve("object_arrays"))),
                () -> new NamesDb(Utils.openDb(dir.resolve("names"))),
                () -> new ClassInfoDb(Utils.openDb(dir.resolve("classes")))
        );
    }
}
