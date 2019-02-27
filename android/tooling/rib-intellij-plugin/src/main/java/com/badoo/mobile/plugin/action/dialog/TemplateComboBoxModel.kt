package com.badoo.mobile.plugin.action.dialog

import com.badoo.mobile.plugin.template.Template
import javax.swing.DefaultComboBoxModel

class TemplateComboBoxModel(templates: List<Template>) : DefaultComboBoxModel<Template>(templates.toTypedArray())