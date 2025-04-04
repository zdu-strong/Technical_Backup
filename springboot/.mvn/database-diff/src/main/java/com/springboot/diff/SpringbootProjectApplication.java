package com.springboot.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.uuid.Generators;
import com.google.cloud.spanner.InstanceId;
import com.google.cloud.spanner.InstanceInfo;
import com.google.cloud.spanner.SpannerOptions;

@SpringBootApplication
public class SpringbootProjectApplication {

    /**
     * Entry point for the entire program
     *
     * @param args
     */
    public static void main(String[] args) {
        if (isTestEnvironment()) {
            return;
        }

        if (isOnlyResetDatabase(args)) {
            return;
        }

        checkSupportDatabase();

        var isCreateChangeLogFile = false;

        while (true) {
            var newDatabaseName = getANewDatabaseName();
            var oldDatabaseName = getANewDatabaseName();
            var hasCleanDatabase = false;
            try {
                createDatabase(oldDatabaseName);
                createDatabase(newDatabaseName);
                buildNewDatabase(newDatabaseName);
                var isCreateChangeLogFileOfThis = diffDatabase(newDatabaseName, oldDatabaseName);
                deleteDatabase(oldDatabaseName);
                deleteDatabase(newDatabaseName);

                hasCleanDatabase = true;

                if (!isCreateChangeLogFileOfThis) {
                    break;
                } else {
                    isCreateChangeLogFile = true;
                }
            } finally {
                try {
                    if (!hasCleanDatabase) {
                        deleteDatabase(oldDatabaseName);
                        deleteDatabase(newDatabaseName);
                    }
                } catch (Throwable e) {
                    // do nothing
                }
            }
        }
        clean();
        if (!isCreateChangeLogFile) {
            System.out.println("\nAn empty changelog file was generated, so delete it.");
        } else {
            System.out.println("\nAn changelog file was generated!");
        }
    }

    public static boolean isOnlyResetDatabase(String[] args) {
        checkSupportDatabase();
        if (args != null && Arrays.asList(args).contains("--onlyResetDatabase")) {
            resetDatabase();
            clean();
            return true;
        } else {
            return false;
        }
    }

    @SneakyThrows
    private static void resetDatabase() {
        var databaseName = getDatabaseName();
        deleteDatabase(databaseName);
        createDatabase(databaseName);
    }

    @SneakyThrows
    public static void buildNewDatabase(String newDatabaseName) {
        var availableServerPort = getUnusedPort();

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile spring-boot:run --define database.name="
                + newDatabaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        processBuilder.environment().put("SERVER_PORT", String.valueOf(availableServerPort));
        processBuilder.environment().put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
        processBuilder.environment().put("SPRING_LIQUIBASE_ENABLED", "false");
        processBuilder.environment().put("PROPERTIES_STORAGE_ROOT_PATH", "target/diff-for-new-database");
        var process = processBuilder.start();
        while (true) {
            var url = new URIBuilder("http://127.0.0.1:" + availableServerPort).build();
            try {
                new RestTemplate().getForObject(url, String.class);
                break;
            } catch (Throwable e) {
                // do nothing
            }
            if (process.isAlive()) {
                Thread.sleep(1000);
                continue;
            } else {
                throw new RuntimeException("Service startup failed");
            }
        }

        for (var i = 1000; i > 0; i--) {
            Thread.sleep(1);
        }
        destroy(process.toHandle());
    }

    @SneakyThrows
    public static String getFilenameExtensionOfChangeLog() {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            return ".xml";
        }
        return ".sql";
    }

    @SneakyThrows
    public static boolean diffDatabase(String newDatabaseName, String oldDatabaseName) {
        var today = FastDateFormat.getInstance("yyyy.MM.dd.HH.mm.ss", TimeZone.getTimeZone("UTC"))
                .format(new Date());
        var filePathOfDiffChangeLogFile = Paths
                .get(getBaseFolderPath(), "src/main/resources", "liquibase/changelog",
                        today.substring(0, 10),
                        today + "_changelog." + getDatabaseType().getType() + getFilenameExtensionOfChangeLog())
                .normalize().toString().replaceAll(Pattern.quote("\\"), "/");
        var isCreateFolder = !existFolder(Paths.get(filePathOfDiffChangeLogFile, "..").normalize().toString());

        if (isCreateFolder) {
            Paths.get(filePathOfDiffChangeLogFile, "..").normalize().toFile().mkdirs();
        }

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add(
                StringUtils.join(List.of(
                        "mvn clean compile",
                        "liquibase:update",
                        "liquibase:diff",
                        "--define database.name=\"" + oldDatabaseName + "\"",
                        "--define database.liquibase.reference.database.name=\"" + newDatabaseName + "\"",
                        "--define database.liquibase.diff.changelog.file=" + filePathOfDiffChangeLogFile
                ), StringUtils.SPACE)
        );
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        processBuilder.environment().put("PROPERTIES_STORAGE_ROOT_PATH", "target/diff-for-old-database");
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }

        var isEmptyOfDiffChangeLogFile = isEmptyOfDiffChangeLogFile(filePathOfDiffChangeLogFile);

        if (isEmptyOfDiffChangeLogFile) {
            if (isCreateFolder) {
                FileUtils.deleteQuietly(new File(filePathOfDiffChangeLogFile, ".."));
            } else {
                FileUtils.deleteQuietly(new File(filePathOfDiffChangeLogFile));
            }
        }
        var fileOfDerbyLog = new File(getBaseFolderPath(), "derby.log");
        FileUtils.deleteQuietly(fileOfDerbyLog);
        var isCreateChangeLogFile = !isEmptyOfDiffChangeLogFile;
        if (isCreateChangeLogFile) {
            replaceDatetimeColumnType(new File(filePathOfDiffChangeLogFile));
        }
        return isCreateChangeLogFile;
    }

    @SneakyThrows
    private static boolean isEmptyOfDiffChangeLogFile(String filePathOfDiffChangeLogFile) {
        if (!new File(filePathOfDiffChangeLogFile).exists()) {
            return true;
        }
        String textContentOfDiffChangeLogFile;
        try (var input = new FileInputStream(new File(filePathOfDiffChangeLogFile))) {
            textContentOfDiffChangeLogFile = IOUtils.toString(input, StandardCharsets.UTF_8);
        }
        var isEmptyOfDiffChangeLogFile = !textContentOfDiffChangeLogFile.contains("-- changeset ");
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            isEmptyOfDiffChangeLogFile = !textContentOfDiffChangeLogFile.contains("<changeSet ");
        }
        return isEmptyOfDiffChangeLogFile;
    }

    public static void destroy(ProcessHandle hanlde) {
        hanlde.descendants().forEach((s) -> destroy(s));
        hanlde.destroy();
    }

    @SneakyThrows
    public static void clean() {
        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile");
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    @SneakyThrows
    public static void deleteDatabase(String databaseName) {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            deleteDatabaseOfSpanner(databaseName);
            return;
        }
        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile sql:execute@delete --define database.name=" + databaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    @SneakyThrows
    private static void deleteDatabaseOfSpanner(String databaseName) {
        createDatabase(databaseName);
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            var databaseAdminClient = spanner.getDatabaseAdminClient();
            databaseAdminClient.dropDatabase(instance, databaseName);
        }
    }

    @SneakyThrows
    private static void createDatabaseOfSpanner(String databaseName) {
        createInstanceOfSpanner();
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            try {
                spanner.getDatabaseAdminClient().createDatabase(instance, databaseName, new ArrayList<String>()).get();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    private static void createInstanceOfSpanner() {
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            try {
                spanner.getInstanceAdminClient()
                        .createInstance(InstanceInfo.newBuilder(InstanceId.of(project, instance)).build()).get();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    @SneakyThrows
    public static void createDatabase(String databaseName) {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            createDatabaseOfSpanner(databaseName);
            return;
        }

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile sql:execute@create --define database.name=" + databaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    public static String getBaseFolderPath() {
        return new File(".").getAbsolutePath();
    }

    public static int getUnusedPort() {
        for (var i = 1000 * 10; i < 65535; i++) {
            try (var s = new ServerSocket(i)) {
                return i;
            } catch (IOException e) {
                continue;
            }
        }
        throw new RuntimeException("Not Implemented");
    }

    public static boolean existFolder(String folderPath) {
        var folder = new File(folderPath);
        var existFolder = folder.isDirectory();
        if (!existFolder) {
            FileUtils.deleteQuietly(folder);
        }
        return existFolder;
    }

    @SneakyThrows
    public static String getANewDatabaseName() {
        var newDatabaseName = "database_"
                + Generators.timeBasedReorderedGenerator().generate().toString().replaceAll(Pattern.quote("-"), "_");
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            Thread.sleep(2);
            newDatabaseName = "database_"
                    + FastDateFormat.getInstance("yyyyMMddHHmmssSSS", TimeZone.getTimeZone(ZoneId.of("UTC")))
                    .format(new Date());
        }
        return newDatabaseName;
    }

    @SneakyThrows
    public static boolean isTestEnvironment() {
        try (var input = new ClassPathResource("application.yml").getInputStream()) {
            var isTestEnvironmentString = new YAMLMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8)).get("properties")
                    .get("storage").get("root").get("path").asText();
            var isTestEnvironment = "defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a".equals(isTestEnvironmentString);
            return isTestEnvironment;
        }
    }

    @SneakyThrows
    private static void replaceDatetimeColumnType(File file) {
        if (getDatabaseType() != SupportDatabaseTypeEnum.MYSQL) {
            return;
        }
        var textList = FileUtils.readLines(file, StandardCharsets.UTF_8);
        textList = textList.stream().map(s -> s.replaceAll(Pattern.quote(" datetime "), " datetime(6) ")).toList();
        FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), textList);
    }

    @SneakyThrows
    private static SupportDatabaseTypeEnum getDatabaseType() {
        var driver = getDatabaseDriver();
        var databasePlatform = getDatabasePlatform();
        var supportDatabase = JinqStream.from(Arrays.asList(SupportDatabaseTypeEnum.values()))
                .where(s -> s.getDriver().equals(driver) && databasePlatform.contains(s.getPlatform()))
                .getOnlyValue();
        return supportDatabase;
    }

    @SneakyThrows
    private static void checkSupportDatabase() {
        var driver = getDatabaseDriver();
        var databasePlatform = getDatabasePlatform();
        var hasMatch = Arrays.stream(SupportDatabaseTypeEnum.values())
                .anyMatch(s -> s.getDriver().equals(driver) && databasePlatform.contains(s.getPlatform()));
        if (!hasMatch) {
            throw new RuntimeException(
                    "Only support database " + "[" + String.join(", ", Arrays.stream(SupportDatabaseTypeEnum.values())
                            .map(s -> s.getType()).toList().toArray(ArrayUtils.EMPTY_STRING_ARRAY)) + "]");
        }
    }

    @SneakyThrows
    private static String getDatabaseDriver() {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var driver = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.driver")
                    .asText();
            return driver;
        }
    }

    @SneakyThrows
    private static String getDatabasePlatform() {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var platform = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.platform")
                    .asText();
            return platform;
        }
    }

    @SneakyThrows
    private static String getDatabaseJdbcUrl() {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var databaseJdbcUrl = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.jdbc.url")
                    .asText();
            return databaseJdbcUrl;
        }
    }

    @SneakyThrows
    private static String getSpannerProject() {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern
                .compile("(?<=" + Pattern.quote("/projects/") + ")[^/]+(?=" + Pattern.quote("/instances/") + ")");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner project");
    }

    private static String getSpannerInstance() {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern.compile("(?<=" + Pattern.quote("/instances/") + ")[^/]+$");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner instance");
    }

    @SneakyThrows
    private static String getDatabaseName() {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var databaseName = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.name")
                    .asText();
            return databaseName;
        }
    }

}
