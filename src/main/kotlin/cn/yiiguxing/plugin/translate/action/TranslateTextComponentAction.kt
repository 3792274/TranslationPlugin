package cn.yiiguxing.plugin.translate.action

import cn.yiiguxing.plugin.translate.Settings
import cn.yiiguxing.plugin.translate.TranslationUiManager
import cn.yiiguxing.plugin.translate.util.getSelectionFromCurrentCaret
import cn.yiiguxing.plugin.translate.util.splitWord
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actions.TextComponentEditorAction

/**
 * 文本组件（如提示气泡、输入框……）翻译
 *
 * Created by Yii.Guxing on 2017/9/11
 */
class TranslateTextComponentAction : TextComponentEditorAction(Handler()) {

    init {
        isEnabledInModalContext = true
    }

    private class Handler : EditorActionHandler() {

        private val mSettings = Settings.getInstance()!!

        public override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            val text = when {
                editor.selectionModel.hasSelection() -> editor.selectionModel.selectedText
                !editor.isViewer                     -> {
                    val textRange = editor.getSelectionFromCurrentCaret(mSettings.autoSelectionMode)
                    textRange?.let { editor.document.getText(it) }
                }
                else                                 -> null
            }

            text.splitWord()?.let {
                TranslationUiManager.getInstance().showTranslationDialog(editor.project).query(it)
            }
        }

        public override fun isEnabledForCaret(editor: Editor, caret: Caret, dataContext: DataContext?) =
                when {
                    editor.selectionModel.hasSelection() -> !editor.selectionModel.selectedText.isNullOrBlank()
                    !editor.isViewer                     -> {
                        val textRange = editor.getSelectionFromCurrentCaret(mSettings.autoSelectionMode)
                        textRange?.let { editor.document.getText(it).isNotBlank() } ?: false
                    }
                    else                                 -> false
                }
    }

}