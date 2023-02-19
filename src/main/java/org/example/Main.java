package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.compress.utils.FileNameUtils;
import org.example.archive.ApacheBz2Decompressor;
import org.example.model.TagCount;
import org.example.parser.StaxXmlParser;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

@Slf4j
public class Main {
    public static void main(String[] args) throws ParseException, IOException, XMLStreamException {
        var commandLine = parseCommandLine(args);
        var archive = getCheckedArchiveFile(commandLine);
        var mode = getSearchMode(commandLine);

        var bz2Decompressor = new ApacheBz2Decompressor();
        var xmlParser = new StaxXmlParser();

        InputStream archiveInputStream = bz2Decompressor.decompressBz2Archive(archive);

        Map<String, Integer> tagCountMap;
        switch (mode) {
            case K -> tagCountMap = xmlParser.getCountOfTag(archiveInputStream, "tag", "k");
            case USER -> tagCountMap = xmlParser.getCountOfTag(archiveInputStream, "node", "user");
            default -> throw new IllegalArgumentException();
        }

        List<TagCount> tags = tagCountMap.entrySet()
                .stream()
                .map(userCount -> new TagCount(userCount.getKey(), userCount.getValue()))
                .sorted(Comparator.comparingInt(TagCount::count))
                .toList();

        printResult(tags);
    }

    private static SearchMode getSearchMode(CommandLine commandLine) {
        try {
            return SearchMode.valueOf(commandLine.getOptionValue("m").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            log.error("Available argument for -m flag is: {}",
                    Arrays.toString(
                            Arrays.stream(SearchMode.values())
                                    .map(SearchMode::toString)
                                    .map(String::toLowerCase)
                                    .toArray()));
            throw exception;
        }
    }

    private static File getCheckedArchiveFile(CommandLine commandLine) {
        var archive = new File(commandLine.getOptionValue("f"));
        if (checkFile(archive)) {
            throw new IllegalArgumentException("file must be bz2 archive");
        }

        return archive;
    }

    private static CommandLine parseCommandLine(String[] args) throws ParseException {
        var parser = new DefaultParser();

        var fileOption = new Option("f", "file", true, "path to bz2 archive file");
        var modeOption = new Option("m", "mode", true, "k or user");
        var options = new Options();
        options.addOption(fileOption);
        options.addOption(modeOption);

        return parser.parse(options, args, false);
    }

    private static boolean checkFile(File archive) {
        return !archive.exists() || !archive.isFile() || !FileNameUtils.getExtension(archive.getPath()).equals("bz2");
    }

    private static void printResult(List<TagCount> userEdits) {
        log.info("Всего записей: {}", userEdits.size());
        for (TagCount tagCount : userEdits) {
            log.info(tagCount.name() + " - " + tagCount.count());
        }
    }
}
