package com.ifree.codegpt.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.ifree.codegpt.generator.CodeExplainer;
import org.jetbrains.annotations.NotNull;

/**
 * @author tian
 */
public class ExplainCodeAction extends BaseTextPreHandler {
    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }

    @Override
    CodeExplainer.ExplainerType getExplainerType() {
        return CodeExplainer.ExplainerType.CODE_EXPLAIN;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }
}

