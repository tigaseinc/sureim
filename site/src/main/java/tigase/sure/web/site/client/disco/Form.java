/*
 * Sure.IM site - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.sure.web.site.client.disco;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.forms.*;
import tigase.sure.web.site.client.ClientFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author andrzej
 */
public class Form
		extends Composite {

	private final ClientFactory factory;

	private final FlexTable layout;
	private JabberDataElement data;
	private boolean displayFieldDescription = true;

	public Form(ClientFactory factory_) {
		this.factory = factory_;

		layout = new FlexTable();
		layout.addStyleName("jabberDataForm");

		initWidget(layout);
	}

	public void setDisplayFieldDescription(boolean displayFieldDescription) {
		this.displayFieldDescription = displayFieldDescription;
	}

	public void reset() {
		layout.clear();
	}

	public void setColumnWidth(int column, String width) {
		for (int row = 0; row < layout.getRowCount(); row++) {
			layout.getFlexCellFormatter().setWidth(row, column, "40%");
		}
	}

	public JabberDataElement getData() throws JaxmppException {
		int row = 0;

		String instructions = data.getInstructions();
		if (instructions != null) {
			row++;
		}

		for (AbstractField field : data.getFields()) {
			if ("hidden".equals(field.getType())) {
				continue;
			}

			Widget w = layout.getWidget(row, 1);

			if ("boolean".equals(field.getType())) {
				CheckBox checkbox = (CheckBox) w;
				((BooleanField) field).setFieldValue(checkbox.getValue());
			} else if ("text-single".equals(field.getType()) || "jid-single".equals(field.getType())) {
				TextBox textBox = (TextBox) w;
				String val = textBox.getValue();
				if ("text-single".equals(field.getType())) {
					((TextSingleField) field).setFieldValue(val);
				} else if ("jid-single".equals(field.getType())) {
					JID jid = val == null ? null : JID.jidInstance(val);
					((JidSingleField) field).setFieldValue(jid);
				}
			} else if ("text-multi".equals(field.getType())) {
				TextArea textArea = (TextArea) w;
				String val = textArea.getValue();
				String[] values =
						val != null ? val.split("\n") : new String[0];//((TextMultiField) field).getFieldValue();
				((TextMultiField) field).setFieldValue(values);
			} else if ("jid-multi".equals(field.getType())) {
				TextArea textArea = (TextArea) w;
				String val = textArea.getValue();
				String[] values = val != null
								  ? val.split("\n")
								  : new String[0];//((TextMultiField) field).getFieldValue();
				List<JID> jids = new ArrayList<JID>();
				for (String v : values) {
					jids.add(JID.jidInstance(v));
				}
				((JidMultiField) field).setFieldValue(jids.toArray(new JID[jids.size()]));
			} else if ("text-private".equals(field.getType())) {
				PasswordTextBox textBox = (PasswordTextBox) w;
				((TextPrivateField) field).setFieldValue(textBox.getValue());
			} else if ("list-single".equals(field.getType())) {
				ListBox listBox = (ListBox) w;
				int idx = listBox.getSelectedIndex();
				String val = null;
				if (idx >= 0) {
					val = listBox.getValue(idx);
				}
				((ListSingleField) field).setFieldValue(val);
			} else if ("list-multi".equals(field.getType())) {
				ListBox listBox = new ListBox();
				listBox.setMultipleSelect(true);
				List<String> selections = new ArrayList<String>();
				for (int idx = 0; idx < listBox.getItemCount(); idx++) {
					if (listBox.isItemSelected(row)) {
						selections.add(listBox.getValue(idx));
					}
				}
				((ListMultiField) field).setFieldValue(selections.toArray(new String[selections.size()]));
			} else {
				w = new Label(field.getValue());
			}

			row++;
		}

		return data;
	}

	public void setData(JabberDataElement data) throws JaxmppException {
		int row = 0;
		String instructions = data.getInstructions();
		layout.removeAllRows();
		if (instructions != null) {
			//layout.setText(row, 0, "Instructions");
			Label instructionsLabel = new Label(instructions);
			instructionsLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			layout.setWidget(row, 0, instructionsLabel);
			layout.getFlexCellFormatter().setColSpan(row, 0, 2);
			row++;
		}

		this.data = data;

		for (AbstractField field : data.getFields()) {
			String name = field.getLabel();
			if (name == null) {
				name = field.getVar();
			}

			if ("hidden".equals(field.getType())) {
				continue;
			}

			Label fieldLabel = new Label(name);
			fieldLabel.setWidth("180px");
			layout.setWidget(row, 0, fieldLabel);

			Widget w = null;

			if ("boolean".equals(field.getType())) {
				CheckBox checkbox = new CheckBox();
				if (field.getFieldValue() != null) {
					checkbox.setValue(Boolean.parseBoolean(field.getFieldValue().toString()));
				}
				w = checkbox;
			} else if ("text-single".equals(field.getType()) || "jid-single".equals(field.getType())) {
				TextBox textBox = new TextBox();
				if (field.getFieldValue() != null) {
					textBox.setValue(field.getFieldValue().toString());
				}
				w = textBox;
			} else if ("text-multi".equals(field.getType())) {
				TextArea textArea = new TextArea();
				String[] values = ((TextMultiField) field).getFieldValue();
				if (values != null) {
					String value = null;
					for (String v : values) {
						if (value != null) {
							value += "\n";
						} else {
							value = "";
						}
						value += v;
					}
					if (value != null) {
						textArea.setValue(value);
					}
				}
				w = textArea;
			} else if ("jid-multi".equals(field.getType())) {
				TextArea textArea = new TextArea();
				JID[] values = ((JidMultiField) field).getFieldValue();
				if (values != null) {
					String value = null;
					for (JID v : values) {
						if (value != null) {
							value += "\n";
						} else {
							value = "";
						}
						if (v != null) {
							value += v.toString();
						}
					}
					if (value != null) {
						textArea.setValue(value);
					}
				}
				w = textArea;
			} else if ("text-private".equals(field.getType())) {
				PasswordTextBox textBox = new PasswordTextBox();
				if (field.getFieldValue() != null) {
					textBox.setText(field.getFieldValue().toString());
				}
				w = textBox;
			} else if ("list-single".equals(field.getType())) {
				ListBox listBox = new ListBox();
				List<Element> options = ((ListSingleField) field).getChildren("option");
				for (Element option : options) {
					String label = option.getAttribute("label");
					if (label == null) {
						label = option.getFirstChild().getValue();
					}
					listBox.addItem(label, option.getFirstChild().getValue());
				}
				if (field.getFieldValue() != null) {
					for (int idx = 0; idx < listBox.getItemCount(); idx++) {
						if (field.getFieldValue().equals(listBox.getValue(idx))) {
							listBox.setSelectedIndex(idx);
						}
					}
				}
				w = listBox;
			} else if ("list-multi".equals(field.getType())) {
				ListBox listBox = new ListBox();
				listBox.setMultipleSelect(true);
				List<Element> options = ((ListMultiField) field).getChildren("option");
				for (Element option : options) {
					String label = option.getAttribute("label");
					if (label == null) {
						label = option.getFirstChild().getValue();
					}
					listBox.addItem(label, option.getFirstChild().getValue());
				}
				String[] values = ((ListMultiField) field).getFieldValue();
				if (values != null) {
					HashSet<String> vals = new HashSet<String>();
					for (String v : values) {
						vals.add(v);
					}
					for (int idx = 0; idx < listBox.getItemCount(); idx++) {
						String val = listBox.getValue(idx);
						if (vals.contains(val)) {
							listBox.setSelectedIndex(idx);
						}
					}
				}
				w = listBox;
			} else {
				Label label = new Label();
				if (field.getFieldValue() != null) {
					label.setText(field.getFieldValue().toString());
				}
				w = label;
			}

			layout.setWidget(row, 1, w);

			if (displayFieldDescription) {
				Label desc = new Label();
				if (field.getDesc() != null) {
					desc.setText(field.getDesc());
				}
				layout.setWidget(row, 2, desc);
			}

			row++;
		}

	}
}
