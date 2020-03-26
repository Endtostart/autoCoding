package idea.plugin.autocoding.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.DocumentRunnable;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.source.PsiClassImpl;
import idea.plugin.autocoding.dto.LineDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContextUtil {

    private static Logger logger = LoggerFactory.getLogger(ContextUtil.class);

    /**
     * 获取当当前行
     * @param lines
     * @param position
     * @return
     */
    public static String getCurrentText(List<String> lines, LogicalPosition position) {
        String currentText = lines.get(position.line);
        currentText = currentText.trim();
        return currentText;
    }

    /**
     * 获取之前一行
     * @param lines
     * @param position
     * @return
     */
    public static LineDto getPreLine(List<String> lines, LogicalPosition position) {
        for (int i = position.line; i >= 0; i--) {
            String line = format(lines.get(i));
            LineDto lineDto = buildLine(line, i);
            if (lineDto != null) {
                return lineDto;
            }
        }
        return null;
    }

    /**
     * 生成插入代码
     *
     * @param pojo
     * @param currentFileElement
     * @param sourceName
     * @return
     */
    public static String insertSetterCode(Project project, Document document, LineDto pojo,
                                          PsiElement currentFileElement, Integer offset, String sourceName) {
        logger.info("生成 set 代码");
        String code = getInsertLineString(currentFileElement, pojo, sourceName);
        insertString(document, code, offset, project);
        return code;
    }

    /**
     * 删除行
     * @param project
     * @param document
     * @param start
     * @param end
     */
    public static void deleteLine(Project project, Document document, int start, int end) {
        ApplicationManager.getApplication().runWriteAction(new DocumentRunnable(document, null) {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            document.deleteString(start, end);
                        } catch (Exception ignored) {
                        }
                    }
                }, "", document);
            }
        });
    }


    /**
     * 插入代码
     * @param document
     * @param insertLine
     * @param offset
     * @param project
     */
    public static void insertString(Document document, String insertLine, int offset, Project project) {

        ApplicationManager.getApplication().runWriteAction(new DocumentRunnable(document, null) {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            document.insertString(offset, insertLine);
                        } catch (Exception ignored) {
                        }
                    }
                }, insertLine, document);
            }
        });

    }

    /**
     * 生成代码
     * @param data
     * @param lineDto
     * @param srouceName
     * @return
     */
    private static String getInsertLineString(PsiElement data, LineDto lineDto, String srouceName) {
        List<String> retList = Lists.newArrayList();
        PsiField[] fields = null;
        if(data instanceof ClsClassImpl){
            ClsClassImpl clazz = (ClsClassImpl) data;
            fields = clazz.getAllFields();
        }else if(data instanceof PsiClassImpl){
            PsiClassImpl clazz = (PsiClassImpl) data;
            fields = clazz.getAllFields();
        }

        if (fields != null && fields.length > 0) {
            for (PsiField field : fields) {
                String name = field.getName();
                String preLine = lineDto.getPreSpace() + lineDto.getInstanceName() + ".";
                String firstUperName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                String lineText = preLine + "set" + firstUperName + "(" + srouceName + ".get" + firstUperName + "());";
                retList.add(lineText);
            }
        }

        String line = StringUtils.EMPTY;
        for (String s : retList) {
            line += s;
            line +=  "\n";
        }
        logger.info("insert line is :{}", line);
        return line;
    }


    /**
     * 构建line对象
     * @param line
     * @param lineNum
     * @return
     */
    private static LineDto buildLine(String line,Integer lineNum) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }

        List<String> lines = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(line);
        if (!isNewInstanceLine(lines)) {
            return null;
        }

        LineDto lineDto = new LineDto();
        lineDto.setClassName(lines.get(0));
        lineDto.setInstanceName(lines.get(1));
        lineDto.setLineNum(lineNum);
        lineDto.setStartPosition(line.indexOf(lineDto.getClassName()));
        lineDto.setPreSpace(line.substring(0, lineDto.getStartPosition()));
        return lineDto;
    }

    /**
     * 判断当前行是否是new instance
     * @param lines
     * @return
     */
    private static boolean isNewInstanceLine(List<String> lines) {
        if (lines.size() < 8) {
            return false;
        }
        if (lines.get(0).equals(lines.get(4)) && lines.get(2).equals("=")
                && lines.get(3).equals("new") && lines.get(5).equals("(")
                && lines.get(6).equals(")") && lines.get(7).equals(";")) {
            return true;
        }
        return false;
    }

    /**
     * line 格式化
     * @param line
     * @return
     */
    private static String format(String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        line = line.replace("=", " = ");
        line = line.replace("(", " ( ");
        line = line.replace(")", " ) ");
        line = line.replace(";", " ; ");
        return line;
    }
}
