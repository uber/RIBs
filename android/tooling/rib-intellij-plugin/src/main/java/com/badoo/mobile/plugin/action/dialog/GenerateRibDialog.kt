package com.badoo.mobile.plugin.action.dialog

import com.badoo.mobile.plugin.template.Template
import com.badoo.mobile.plugin.template.Token
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridConstraints.*
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import java.awt.Dimension
import javax.swing.*

class GenerateRibDialog(private val listener: Listener,
                        templates: List<Template>) : DialogWrapper(null) {

    interface Listener {
        fun onGenerateClicked(tokenValues: Map<String, String>, templateId: String)
    }

    private lateinit var contentPanel: JPanel
    private lateinit var paramsPanel: JPanel
    private lateinit var templateChooser: JComboBox<*>

    private var tokenTextFields: Map<String, JTextField>? = null

    init {
        init()
        templateChooser.model = TemplateComboBoxModel(templates)
        populateTokenInputs(templates.first().tokens)
    }

    private fun populateTokenInputs(tokens: List<Token>) {
        paramsPanel.removeAll()
        paramsPanel.layout = GridLayoutManager(tokens.size + 1, 2)

        val fields = mutableMapOf<String, JTextField>()

        tokens.forEachIndexed { index, token ->
            paramsPanel.add(JLabel().apply {
                text = token.name
            }, createConstraints(
                row = index,
                column = 0,
                preferredSize = Dimension(50, 30)
            ))

            paramsPanel.add(JTextField().apply {
                preferredSize = Dimension(150, 30)
                fields[token.id] = this
            }, createConstraints(
                row = index,
                column = 1,
                fill = FILL_HORIZONTAL,
                horizontalSizePolicy = SIZEPOLICY_CAN_GROW or SIZEPOLICY_WANT_GROW,
                preferredSize = Dimension(150, 30)
            ))
        }

        paramsPanel.add(Spacer(),
            createConstraints(
                row = fields.size,
                column = 0,
                fill = FILL_VERTICAL,
                horizontalSizePolicy = SIZEPOLICY_CAN_SHRINK,
                verticalSizePolicy = SIZEPOLICY_CAN_GROW or SIZEPOLICY_WANT_GROW
            ))

        fields.entries.firstOrNull()?.value?.requestFocus()

        tokenTextFields = fields
    }

    override fun doOKAction() {
        val selectedTemplate = templateChooser.selectedItem as Template
        val tokenValues = tokenTextFields?.map {
            it.key to it.value.text
        }?.toMap() ?: throw IllegalStateException("Tokens are not initialized")

        if (tokenValues.any { it.value.isBlank() }) {
            JOptionPane.showMessageDialog(null, "All fields are required")
            return
        }

        super.doOKAction()
        listener.onGenerateClicked(tokenValues, selectedTemplate.id)
    }

    override fun createCenterPanel(): JComponent? = contentPanel

    private fun createConstraints(
        row: Int,
        column: Int,
        fill: Int = FILL_NONE,
        horizontalSizePolicy: Int = SIZEPOLICY_FIXED,
        verticalSizePolicy: Int = SIZEPOLICY_FIXED,
        preferredSize: Dimension? = null
    ) = GridConstraints(
        row,
        column,
        1,
        1,
        0,
        fill,
        horizontalSizePolicy,
        verticalSizePolicy,
        null,
        preferredSize,
        null
    )
}
