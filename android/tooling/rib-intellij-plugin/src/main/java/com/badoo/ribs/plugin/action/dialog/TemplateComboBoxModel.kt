package com.badoo.ribs.plugin.action.dialog

import com.badoo.ribs.plugin.template.Template
import javax.swing.DefaultComboBoxModel

class TemplateComboBoxModel(templates: List<Template>) : DefaultComboBoxModel<Template>(templates.toTypedArray())