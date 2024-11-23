package com.springboot.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.uuid.Generators;
import com.google.cloud.spanner.InstanceId;
import com.google.cloud.spanner.InstanceInfo;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;

@SpringBootApplication
public class SpringbootProjectApplication {

    /**
     * Entry point for the entire program
     * 
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        if (isTestEnviroment()) {
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

    public static boolean isOnlyResetDatabase(String[] args)
            throws IOException, InterruptedException, SpannerException, ExecutionException {
        checkSupportDatabase();
        if (args != null && Arrays.asList(args).contains("--onlyResetDatabase")) {
            resetDatabase();
            clean();
            return true;
        } else {
            return false;
        }
    }

    private static void resetDatabase()
            throws JsonMappingException, JsonProcessingException, IOException, InterruptedException, SpannerException,
            ExecutionException {
        var databaseName = getDatabaseName();
        deleteDatabase(databaseName);
        createDatabase(databaseName);
    }

    public static void buildNewDatabase(String newDatabaseName)
            throws IOException, InterruptedException, URISyntaxException {
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

    public static String getFilenameExtensionOfChangeLog()
            throws JsonMappingException, JsonProcessingException, IOException {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            return ".xml";
        }
        return ".sql";
    }

    public static boolean diffDatabase(String newDatabaseName, String oldDatabaseName)
            throws IOException, InterruptedException {
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
                "mvn clean compile liquibase:update liquibase:diff --define database.name="
                        + oldDatabaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        processBuilder.environment().put("LIQUIBASE_DIFF_CHANGELOG_FILE", filePathOfDiffChangeLogFile);
        processBuilder.environment().put("PROPERTIES_STORAGE_ROOT_PATH", "target/diff-for-old-database");
        processBuilder.environment().put("LIQUIBASE_REFERENCE_DATABASE_NAME", newDatabaseName);
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

    private static boolean isEmptyOfDiffChangeLogFile(String filePathOfDiffChangeLogFile)
            throws FileNotFoundException, IOException {
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

    public static void clean() throws IOException, InterruptedException {
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

    public static void deleteDatabase(String databaseName)
            throws IOException, InterruptedException, SpannerException, ExecutionException {
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

    private static void deleteDatabaseOfSpanner(String databaseName)
            throws JsonMappingException, JsonProcessingException, IOException, InterruptedException, SpannerException,
            ExecutionException {
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

    private static void createDatabaseOfSpanner(String databaseName) throws JsonMappingException,
            JsonProcessingException, IOException, SpannerException, InterruptedException, ExecutionException {
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

    private static void createInstanceOfSpanner() throws JsonMappingException, JsonProcessingException, IOException,
            SpannerException, InterruptedException, ExecutionException {
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

    public static void createDatabase(String databaseName)
            throws IOException, InterruptedException, SpannerException, ExecutionException {
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

    public static String getANewDatabaseName()
            throws JsonMappingException, JsonProcessingException, IOException, InterruptedException {
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

    public static boolean isTestEnviroment() throws IOException {
        try (var input = new ClassPathResource("application.yml").getInputStream()) {
            var isTestEnviromentString = new YAMLMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8)).get("properties")
                    .get("storage").get("root").get("path").asText();
            var isTestEnviroment = "defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a".equals(isTestEnviromentString);
            return isTestEnviroment;
        }
    }

    private static void replaceDatetimeColumnType(File file) throws IOException {
        if (getDatabaseType() != SupportDatabaseTypeEnum.MYSQL) {
            return;
        }
        var textList = FileUtils.readLines(file, StandardCharsets.UTF_8);
        textList = textList.stream().map(s -> s.replaceAll(Pattern.quote(" datetime "), " datetime(6) ")).toList();
        FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), textList);
    }

    private static SupportDatabaseTypeEnum getDatabaseType()
            throws JsonMappingException, JsonProcessingException, IOException {
        var driver = getDatabaseDriver();
        var databasePlatform = getDatabasePlatform();
        var supportDatabase = JinqStream.from(Arrays.asList(SupportDatabaseTypeEnum.values()))
                .where(s -> s.getDriver().equals(driver) && databasePlatform.contains(s.getPlatform()))
                .getOnlyValue();
        return supportDatabase;
    }

    private static void checkSupportDatabase() throws IOException {
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

    private static String getDatabaseDriver() throws JsonMappingException, JsonProcessingException, IOException {
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

    private static String getDatabasePlatform() throws JsonMappingException, JsonProcessingException, IOException {
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

    private static String getDatabaseJdbcUrl() throws JsonMappingException, JsonProcessingException, IOException {
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

    private static String getSpannerProject() throws JsonMappingException, JsonProcessingException, IOException {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern
                .compile("(?<=" + Pattern.quote("/projects/") + ")[^/]+(?=" + Pattern.quote("/instances/") + ")");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner project");
    }

    private static String getSpannerInstance() throws JsonMappingException, JsonProcessingException, IOException {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern.compile("(?<=" + Pattern.quote("/instances/") + ")[^/]+$");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner instance");
    }

    private static String getDatabaseName() throws JsonMappingException, JsonProcessingException, IOException {
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
