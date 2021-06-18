package org.sharpler.hprofcrawler;

import org.sharpler.hprofcrawler.backend.LevelDbBuilder;
import org.sharpler.hprofcrawler.inspection.BuildInInspection;
import org.sharpler.hprofcrawler.parser.HprofParser;
import org.sharpler.hprofcrawler.views.TerminalProgress;
import picocli.CommandLine;

import java.io.File;
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

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (!Files.exists(dumpPath)) {
            throw new IllegalArgumentException("File doesn't exist: " + dumpPath);
        }
        Path dbsDir = dumpPath.getParent().resolve("dbs");
        if (!Files.exists(dbsDir)) {
            Files.createDirectory(dbsDir);
        }

        var builder = LevelDbBuilder.of(dbsDir);

        HprofParser parser = new HprofParser(builder);

        File file = dumpPath.toFile();
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

        startTime = System.currentTimeMillis();
        var result = inspection.run(backend, new TerminalProgress());
        finishTime = System.currentTimeMillis();

        System.out.printf("Scan time: %d(ms)%n", finishTime - startTime);

        System.out.println(result);

        return 0;
    }
}
