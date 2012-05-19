/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author andrzej
 */
public class ErrorDialog extends DialogBox {
        
        private final ClientFactory factory;
        
        public ErrorDialog(ClientFactory factory_, String msg) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(factory.baseI18n().error());

                FlexTable table = new FlexTable();
                Label label = new Label(factory.baseI18n().error());
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);
                
                label = new Label(msg);
                table.setWidget(1, 0, label);
                                
                Button close = new Button(factory.baseI18n().close());
                close.setStyleName(factory.theme().style().button());
                close.addStyleName(factory.theme().style().buttonDefault());
                close.addStyleName(factory.theme().style().right());
                table.setWidget(2, 0, close);
                close.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                        
                });
                
                setWidget(table);                
        }
        
}