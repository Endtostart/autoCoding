package idea.plugin.autocoding.action;

import com.google.common.base.Splitter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.source.PsiClassImpl;
import idea.plugin.autocoding.dto.LineDto;
import idea.plugin.autocoding.util.ContextUtil;

import java.util.List;
import java.util.Objects;

public class BeanCopy extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(LangDataKeys.EDITOR);
        PsiFile currentFile = event.getData(LangDataKeys.PSI_FILE);
        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition oldLogicPos = caretModel.getLogicalPosition();
        String text = currentFile.getText();
        List<String> lines = Splitter.on("\n").splitToList(text);
        // 获取之前最近一个 new instance 行
        LineDto line = ContextUtil.getPreLine(lines, oldLogicPos);
        // 获取当前行文本 即：source bean的名称
        String currentText = ContextUtil.getCurrentText(lines, oldLogicPos);

        if (Objects.nonNull(line)) {
            LogicalPosition newStatementPos = new LogicalPosition(line.getLineNum() , line.getStartPosition() + 1);
            LogicalPosition insertPos = new LogicalPosition(line.getLineNum() + 1 , 0 );
            caretModel.moveToLogicalPosition(newStatementPos);
            PsiElement currentFileElement = event.getData(LangDataKeys.PSI_ELEMENT);
            if (currentFileElement instanceof PsiClassImpl || currentFileElement instanceof ClsClassImpl) {
                Document document = PsiDocumentManager.getInstance(event.getProject()).getDocument(currentFile);
                caretModel.moveToLogicalPosition(insertPos);
                Integer offset = caretModel.getOffset();
                // 获取set代码
                String insertText = ContextUtil.insertSetterCode(project, document, line, currentFileElement, offset, currentText);
                // 计算当前行数
                int addLine = insertText.split("\n").length;
                LogicalPosition afterInsertPosition = new LogicalPosition(insertPos.line + addLine, newStatementPos.column);
                // 移动到代码最后
                caretModel.moveToLogicalPosition(afterInsertPosition);

                // 删除当前行
                ContextUtil.deleteLine(project, document, caretModel.getVisualLineStart(), caretModel.getVisualLineEnd());
            }
        }
    }
}
