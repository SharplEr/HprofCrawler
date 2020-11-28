package org.sharpler.hrofcrawler;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.openjdk.jol.info.GraphLayout;
import org.sharpler.hrofcrawler.api.Progress;
import org.sharpler.hrofcrawler.backend.LevelDbBuilder;
import org.sharpler.hrofcrawler.inspection.FindClassWithConstantField;
import org.sharpler.hrofcrawler.parser.HprofParser;

public final class Main {
    public static void main(String... args) throws IOException {
        String dumpPath = "/home/org.sharpler.hrofcrawler.sharpler/idea_dump";
        String dbsDir = "/home/org.sharpler.hrofcrawler.sharpler/dbs";

        var builder = LevelDbBuilder.of(dbsDir);
//                new InHeapBuilder();
        //LevelDbBuilder.of(dbsDir);

        HprofParser parser = new HprofParser(
                builder
        );

        File file = new File(dumpPath);
        long dumpLength = file.length();

        long startTime = System.currentTimeMillis();
        parser.parse(file);

        long finishTime = System.currentTimeMillis();

        System.out.printf("time=%s(ms),size=%s(MB),speed=%s(MB/s)%n",
                finishTime - startTime,
                dumpLength / 1024L / 1024L,
                ((double) dumpLength / (finishTime - startTime)) * (1000.0 / 1024.0 / 1024.0)
        );

        startTime = System.currentTimeMillis();
        var backend = builder.build();
        finishTime = System.currentTimeMillis();

        System.out.printf("Build backend: %s%n", finishTime - startTime);

        System.out.println("classes count = " + backend.getIndex().classesCount());

        System.out.printf("index size = %s %%%n",
                GraphLayout.parseInstance(backend.getIndex()).totalSize() * 100.0 / dumpLength
        );

        System.out.printf("backend size = %s %%%n",
                GraphLayout.parseInstance(backend).totalSize() * 100.0 / dumpLength
        );

        startTime = System.currentTimeMillis();
        var scanResult = backend.scan(
                new FindClassWithConstantField(100),
                new Progress.Dummy()
        );
        finishTime = System.currentTimeMillis();

        System.out.println("scan time " +
                (finishTime - startTime)
        );

        System.out.println(
                "constant instances: " +
                        scanResult.stream()
                                .filter(x -> !x.getConstants().isEmpty())
                                .sorted(Comparator.comparingInt(
                                        (FindClassWithConstantField.Info x) -> x.getClassView().getCount()).reversed())
                                .limit(10)
                                .map(x -> String.format(
                                        "%s:%s:%s", x.getClassView().getName(), x.getClassView().getCount(),
                                        x.getConstants()
                                        )
                                )
                                .collect(Collectors.joining("\n"))

        );
    }
}
