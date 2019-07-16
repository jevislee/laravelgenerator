package com.utility.laravelgenerator.service.impl;

import com.utility.laravelgenerator.util.CamelCaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@Service
public class LaravelGeneratorServiceImpl {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate template;
    
    private String appDir =  System.getProperty("user.dir") + "/target/app/";

    private String keywords = "ADD,ALL,ALTER,ANALYZE,AND,AS,ASC,ASENSITIVE,BEFORE,BETWEEN,BIGINT,BINARY,BLOB,BOTH,BY,CALL,CASCADE,CASE," +
            "CHANGE,CHAR,CHARACTER,CHECK,COLLATE,COLUMN,CONDITION,CONNECTION,CONSTRAINT,CONTINUE,CONVERT,CREATE,CROSS,CURRENT_DATE,CURRENT_TIME," +
            "CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DATABASE,DATABASES,DAY_HOUR,DAY_MICROSECOND,DAY_MINUTE,DAY_SECOND,DEC,DECIMAL,DECLARE,DEFAULT," +
            "DELAYED,DELETE,DESC,DESCRIBE,DETERMINISTIC,DISTINCT,DISTINCTROW,DIV,DOUBLE,DROP,DUAL,EACH,ELSE,ELSEIF,ENCLOSED,ESCAPED,EXISTS,EXIT," +
            "EXPLAIN,FALSE,FETCH,FLOAT,FLOAT4,FLOAT8,FOR,FORCE,FOREIGN,FROM,FULLTEXT,GOTO,GRANT,GROUP,HAVING,HIGH_PRIORITY,HOUR_MICROSECOND,HOUR_MINUTE," +
            "HOUR_SECOND,IF,IGNORE,IN,INDEX,INFILE,INNER,INOUT,INSENSITIVE,INSERT,INT,INT1,INT2,INT3,INT4,INT8,INTEGER,INTERVAL,INTO,IS,ITERATE,JOIN,KEY," +
            "KEYS,KILL,LABEL,LEADING,LEAVE,LEFT,LIKE,LIMIT,LINEAR,LINES,LOAD,LOCALTIME,LOCALTIMESTAMP,LOCK,LONG,LONGBLOB,LONGTEXT,LOOP,LOW_PRIORITY,MATCH," +
            "MEDIUMBLOB,MEDIUMINT,MEDIUMTEXT,MIDDLEINT,MINUTE_MICROSECOND,MINUTE_SECOND,MOD,MODIFIES,NATURAL,NOT,NO_WRITE_TO_BINLOG,NULL,NUMERIC,ON,OPTIMIZE," +
            "OPTION,OPTIONALLY,OR,ORDER,OUT,OUTER,OUTFILE,PRECISION,PRIMARY,PROCEDURE,PURGE,RAID0,RANGE,READ,READS,REAL,REFERENCES,REGEXP,RELEASE,RENAME,REPEAT," +
            "REPLACE,REQUIRE,RESTRICT,RETURN,REVOKE,RIGHT,RLIKE,SCHEMA,SCHEMAS,SECOND_MICROSECOND,SELECT,SENSITIVE,SEPARATOR,SET,SHOW,SMALLINT,SPATIAL,SPECIFIC,SQL," +
            "SQLEXCEPTION,SQLSTATE,SQLWARNING,SQL_BIG_RESULT,SQL_CALC_FOUND_ROWS,SQL_SMALL_RESULT,SSL,STARTING,STRAIGHT_JOIN,TABLE,TERMINATED,THEN,TINYBLOB,TINYINT," +
            "TINYTEXT,TO,TRAILING,TRIGGER,TRUE,UNDO,UNION,UNIQUE,UNLOCK,UNSIGNED,UPDATE,USAGE,USE,USING,UTC_DATE,UTC_TIME,UTC_TIMESTAMP,VALUES,VARBINARY,VARCHAR," +
            "VARCHARACTER,VARYING,WHEN,WHERE,WHILE,WITH,WRITE,X509,XOR,YEAR_MONTH,ZEROFILL";

    public void generate(String tableNames) {
        String[] array = tableNames.trim().split(",");
        StringBuffer routesContent = new StringBuffer();

        //模版文件必须直接放在resources目录下,不能放子目录,否则无法找到
        String modelTempalte = readFile("XXX.php");
        String contrlTemplate = readFile("XXXController.php");
        String routeTemplate = readFile("XXXRoute.php");

        for(String tableName : array) {
            String modelContent = modelTempalte;
            String contrlContent = contrlTemplate;
            String routeContent = routeTemplate;

            String entityName = CamelCaseUtil.camelCaseName(tableName, true);

            //Arrays.asList创建的List不可改变,要通过new ArrayList<>()把所有元素都复制到新的ArrayList对象中操作
            List<String> ignoreColNames = Arrays.asList("id", "created_at", "updated_at", "deleted_at");

            boolean hasCreatedAt = false;
            boolean hasUpdatedAt = false;
            boolean hasDeletedAt = false;

            List<String> colNames = new ArrayList<>();
            List<String> colDbTypes = new ArrayList<>();
            List<String> colJavaTypes = new ArrayList<>();
            List<String> colLengths = new ArrayList<>();

            SqlRowSet rowSet = template.queryForRowSet("select * from " + tableName);
            SqlRowSetMetaData metaData = rowSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                if (!ignoreColNames.contains(metaData.getColumnName(i))) {
                    colNames.add(metaData.getColumnName(i));
                    colDbTypes.add(metaData.getColumnTypeName(i));
                    colJavaTypes.add(metaData.getColumnClassName(i));
                    colLengths.add(String.valueOf(metaData.getPrecision(i)));
                } else if (metaData.getColumnName(i).equals("created_at")) {
                    hasCreatedAt = true;
                } else if (metaData.getColumnName(i).equals("updated_at")) {
                    hasUpdatedAt = true;
                } else if (metaData.getColumnName(i).equals("deleted_at")) {
                    hasDeletedAt = true;
                }
            }

            StringBuffer comments = new StringBuffer("/*\r\n");
            try {
                DatabaseMetaData dbMetaData = template.getDataSource().getConnection().getMetaData();
                ResultSet resultSet = dbMetaData.getColumns(null, null, tableName, null);
                while (resultSet.next()) {
                    String name = resultSet.getString("COLUMN_NAME");
                    String comment = resultSet.getString("REMARKS");
                    if (!ignoreColNames.contains(name)) {
                        comments.append(" * " + name + ": " + comment + "\r\n");
                    }
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
            comments.append(" */");

            modelContent = process(modelContent, entityName, tableName, colNames);
            modelContent = processModel(modelContent, comments.toString(), hasCreatedAt, hasUpdatedAt, hasDeletedAt);
            String modelPath = createPathIfNonexist(appDir + "Models", entityName + ".php");
            writeFile(modelPath, modelContent);

            contrlContent = process(contrlContent, entityName, tableName, colNames);
            contrlContent = processContrl(contrlContent, colNames, colJavaTypes, colLengths);
            contrlContent = processRequest(contrlContent, colNames, colJavaTypes, colLengths);
            String contrlPath = createPathIfNonexist(appDir + "Http/Controllers", entityName + "Controller.php");
            writeFile(contrlPath, contrlContent);

            routeContent = routeContent.replaceAll("@@@table", tableName.toLowerCase());
            routeContent = routeContent.replaceAll("XXX", entityName);
            routesContent.append(routeContent + "\n");
        }

        String routePath = createPathIfNonexist(appDir, "Route.php");
        writeFile(routePath, routesContent.toString());
    }

    private String process(String content, String entityName, String tableName, List<String> colNames) {
        content = content.replaceAll("XXX", entityName);
        content = content.replaceAll("@@@table", tableName);

        StringBuffer buf = new StringBuffer();
        int colCount = colNames.size();
        for(int i = 0; i < colCount; i++) {
            buf.append("'" +  colNames.get(i) + "'");
            if(i < colCount - 1) {
                buf.append(",");
            }
        }
        content = content.replaceAll("@@@fillable", buf.toString());

        return content;
    }

    private String processContrl(String content, List<String> colNames, List<String> colJavaTypes, List<String> colLengths) {
        int colCount = colNames.size();

        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < colCount; i++) {
            String c = colNames.get(i);
            String t = colJavaTypes.get(i);
            String l = colLengths.get(i);

            if(t.endsWith("String") && Integer.parseInt(l) > 20) {
                buf.append("when(isset($data['" + c + "']), function ($query) use ($data) { return $query->where('" + c + "', 'like', '%'.$data['" + c + "'].'%');})->");
            } else {
                buf.append("when(isset($data['" + c + "']), function ($query) use ($data) { return $query->where('" + c + "', '=', $data['" + c + "']);})->");
            }

            if(i < colCount - 1) {
                buf.append("\n        ");
            }
        }
        content = content.replaceAll("@@@fillwhere", Matcher.quoteReplacement(buf.toString()));

        return content;
    }

    private String processModel(String content, String comments, boolean hasCreatedAt, boolean hasUpdatedAt, boolean hasDeletedAt) {
        if(!hasCreatedAt || !hasUpdatedAt) {
            content = content.replaceAll(Matcher.quoteReplacement("public $timestamps"), Matcher.quoteReplacement("//public $timestamps"));
        }

        if(!hasDeletedAt) {
            content = content.replaceAll("use SoftDeletes;", "//use SoftDeletes;");
            content = content.replaceAll(Matcher.quoteReplacement("protected $dates"), Matcher.quoteReplacement("//protected $dates"));
        }

        content = content.replaceAll("@@@comments", comments);
        return content;
    }

    private String processRequest(String content, List<String> colNames, List<String> colJavaTypes, List<String> colLengths) {
        int colCount = colNames.size();

        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < colCount; i++) {
            String c = colNames.get(i);
            String t = colJavaTypes.get(i);
            String l = colLengths.get(i);

            if(t.endsWith("String")) {
                buf.append("'" + c + "' => 'nullable|string|max:" + l + "'");
            } else if(t.endsWith("Integer") || t.endsWith("Byte") || t.endsWith("Long")) {
                buf.append("'" + c + "' => 'nullable|integer'");
            } else if(t.endsWith("Float") || t.endsWith("Double")){
                buf.append("'" + c + "' => 'nullable|numeric'");
            } else {
                buf.append("'" + c + "' => 'nullable'");
            }

            if(i < colCount - 1) {
                buf.append(",\n       ");
            }
        }
        content = content.replaceAll("@@@fillrule", Matcher.quoteReplacement(buf.toString()));

        return content;
    }

    private String readFile(String templateFile) {
        Resource res = new ClassPathResource(templateFile);
        StringBuffer content = new StringBuffer();
        
        try(InputStream in = res.getInputStream();
            InputStreamReader inputreader = new InputStreamReader(in);
            BufferedReader buffreader = new BufferedReader(inputreader)) {
            String line;
            //分行读取  
            while ((line = buffreader.readLine()) != null) {
                if(!line.trim().startsWith("//")) {
                    content.append(line + "\n");
                }
            }
            
            return content.toString();
        } catch(Exception e) {
            logger.error("error", e);
            return "";
        }
    }

    private void writeFile(String path, String content) {
        try(FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        } catch (IOException e) {
            logger.error("error", e);
        }
    }

    private String createPathIfNonexist(String path, String filename) {
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + "/" +filename;
    }

    public String queryTableNames() {
        StringBuffer tableNames = new StringBuffer();
        List<String> list = template.queryForList("SHOW TABLES", String.class);
        for(int i = 0; i < list.size(); i++) {
            tableNames.append(list.get(i));

            if(i < list.size() - 1) {
                tableNames.append(",");
            }
        }
        return tableNames.toString();
    }

    public void testQuery(String sql) {
        template.queryForRowSet(sql);
    }

    public String check() {
        StringBuffer checkResult = new StringBuffer();
        List<String> keywordList = Arrays.asList(keywords.split(","));

        List variables = template.queryForList("SHOW VARIABLES LIKE 'lower_case_table_names'");
        Map map  = (Map)variables.get(0);
        Object lowercase = map.get("Value");
        if(lowercase.toString().equals("0")) {
            checkResult.append("mysql数据库区分大小写,需要在/etc/my.cnf里增加lower_case_table_names=1\n");
        }

        List<String> tableNameList = template.queryForList("SHOW TABLES", String.class);
        for(String tableName : tableNameList) {
            if(keywordList.contains(tableName.toUpperCase())) {
                checkResult.append("表" + tableName + "的名称为关键字;\n");
            }

            SqlRowSet rowSet = template.queryForRowSet("select * from " + tableName);
            SqlRowSetMetaData metaData = rowSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String colName = metaData.getColumnName(i);
                if (keywordList.contains(colName.toUpperCase())) {
                    checkResult.append("表" + tableName + "的字段" + colName + "的名称为关键字;\n");
                }

                /*
                占用空间
                datetime 以 8 个字节储存
                timestamp 只占 4 个字节

                时区
                datetime 不会进行时区的检索
                timestamp 以utc的格式储存， 它会自动检索当前时区并进行转换

                范围
                datetime	'1000-01-01 00:00:00.000000' to '9999-12-31 23:59:59.999999'
                timestamp	'1970-01-01 00:00:01.000000' to '2038-01-19 03:14:07.999999'
                 */
                String colType = metaData.getColumnTypeName(i);
                if(colType.equalsIgnoreCase("datetime")) {
                    checkResult.append("表" + tableName + "的字段" + colName + "的类型为datetime;\n");
                }

                List<String> possibleDateFieldNames = Arrays.asList(
                        "created_at", "updated_at", "deleted_at",
                        "create_at", "update_at", "delete_at",
                        "created_time", "updated_time", "deleted_time",
                        "create_time", "update_time", "delete_time");
                if(possibleDateFieldNames.contains(colName.toLowerCase())) {
                    if(!colType.equalsIgnoreCase("timestamp")) {
                        checkResult.append("表" + tableName + "的字段" + colName + "的类型不为timestamp;\n");
                    }
                }
            }
        }
        return checkResult.toString();
    }
}
