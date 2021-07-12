import 'dart:io';
import 'package:intl/intl.dart';
import 'package:path/path.dart' as path;

void main() async {
  var baseFolderPath =
      path.normalize(path.join(Directory('.').absolute.path)).replaceAll(
            RegExp(RegExp.escape('\\')),
            '/',
          );
  var dateOfToday = DateTime.now().toUtc();
  var folderNameOfDiffChangeLogFile =
      DateFormat('yyyy.MM.dd').format(dateOfToday);
  var fileNameOfDiffChangeLogFile =
      DateFormat('yyyy.MM.dd.HH.mm.ss').format(dateOfToday) + '_changelog.xml';
  var folderPathOfDiffChangeLogFile = path
      .join(
        baseFolderPath,
        'src/main/resources',
        'liquibase/changelog',
        folderNameOfDiffChangeLogFile,
      )
      .replaceAll(
        RegExp(RegExp.escape('\\')),
        '/',
      );
  ;
  var filePathOfDiffChangeLogFile = path
      .join(
        folderPathOfDiffChangeLogFile,
        fileNameOfDiffChangeLogFile,
      )
      .replaceAll(
        RegExp(RegExp.escape('\\')),
        '/',
      );
  ;
  var isCreateFolder = !await Directory(folderPathOfDiffChangeLogFile).exists();

  if (isCreateFolder) {
    await Directory(folderPathOfDiffChangeLogFile).create();
  }
  var process = await Process.start(
    'mvn clean compile liquibase:dropAll liquibase:update liquibase:diff --define database.mysql.platform=org.hibernate.dialect.MySQL8Dialect',
    [],
    runInShell: true,
    mode: ProcessStartMode.inheritStdio,
    workingDirectory: baseFolderPath,
    environment: {
      'LIQUIBASE_DIFF_CHANGELOG_FILE': filePathOfDiffChangeLogFile,
    },
  );
  if (await process.exitCode != 0) {
    throw Exception('Unexpected exception');
  }
  var textContentOfDiffChangeLogFile =
      await File(filePathOfDiffChangeLogFile).readAsString();
  var isEmptyOfDiffChangeLogFile =
      !textContentOfDiffChangeLogFile.contains('</changeSet>');
  if (isEmptyOfDiffChangeLogFile) {
    if (isCreateFolder) {
      await Directory(folderPathOfDiffChangeLogFile).delete(recursive: true);
    } else {
      await File(filePathOfDiffChangeLogFile).delete(recursive: true);
    }
    print('\nAn empty changelog file was generated, so delete it.');
    var filePathOfDerbyLog = path.join(baseFolderPath, 'derby.log');
    var isExistDerbyLogFile = await File(filePathOfDerbyLog).exists();
    if (isExistDerbyLogFile) {
      await File(filePathOfDerbyLog).delete(recursive: true);
    }
  }
}
