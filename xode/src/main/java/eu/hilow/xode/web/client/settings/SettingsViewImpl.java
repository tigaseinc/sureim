/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.settings;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

import eu.hilow.gwt.base.client.AppView;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.events.ServerFeaturesChangedEvent;
import eu.hilow.xode.web.client.events.ServerFeaturesChangedHandler;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;

/**
 *
 * @author andrzej
 */
public class SettingsViewImpl extends ResizeComposite implements SettingsView {
        
        private final ClientFactory factory;

        private VerticalPanel layout;
        private Collection<String> features;
        
//        private final ToogleButton messageArchivingButton;
        
        private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

                public void serverFeaturesChanged(Collection<DiscoInfoModule.Identity> identities, Collection<String> features_) {
                        features = features_;
                }
                
        };
        
        public SettingsViewImpl(ClientFactory factory_) {
                factory = factory_;
                
                AppView appView = new AppView(factory);
                appView.setActionBar(factory.actionBarFactory().createActionBar(this));

                layout = new VerticalPanel();    
                layout.addStyleName("settingsView");

                FlexTable panel = new FlexTable();
                
                Label label = new Label("Security");                
                panel.setWidget(0, 0, label);
                
                label = new Label("Password");
                panel.setWidget(1, 0, label);
                Anchor anchor = new Anchor("Change password");
                panel.setWidget(1, 1, anchor);
                anchor.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                showChangePasswordDlg();
                        }
                        
                });
                
                panel.addStyleName("settingsPanel");
                
                layout.add(panel);
//                messageArchivingButton = new ToogleButton("Enabled", "Disabled");
//                layout.add(messageArchivingButton);
                
                appView.setCenter(layout);
                
                factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);
                
                initWidget(appView);                
        }

        public void refreshItems() {
                
//                messageArchivingButton.setDisabled(features.contains("urn:xmpp:archive:auto"));
                
        }
        
        private void showChangePasswordDlg() {
                final DialogBox dlg = new DialogBox(true);
                
                dlg.setStyleName("dialogBox");
                dlg.setTitle("Change password");
                
                FlexTable table = new FlexTable();
                Label label = new Label("Change password");
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                table.setWidget(0, 0, label);
                label = new Label("New password");
                table.setWidget(1, 0, label);
                final TextBox pass1 = new PasswordTextBox();
                table.setWidget(1, 1, pass1);
                label = new Label("Confirm password");
                table.setWidget(2, 0, label);
                final TextBox pass2 = new PasswordTextBox();
                table.setWidget(2, 1, pass2);
               
                Button cancel = new Button("Cancel");
                cancel.setStyleName(factory.theme().style().button());
                table.setWidget(3, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                dlg.hide();
                        }
                        
                });                
                
                Button ok = new Button("Confirm");
                ok.setStyleName(factory.theme().style().button());
                ok.addStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().right());
                table.setWidget(3, 1, ok);
                ok.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                String p1 = pass1.getText();
                                String p2 = pass2.getText();
                                
                                if (p1 == null || p1.isEmpty() || p2 == null || p2.isEmpty())
                                        return;
                                
                                String username = factory.jaxmpp().getSessionObject().getUserBareJid().getLocalpart();
                                try {
                                        factory.jaxmpp().getModulesManager().getModule(InBandRegistrationModule.class).register(username, p1, null, null);
                                        dlg.hide();
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(SettingsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                        
                });
                                
                dlg.setWidget(table);
                
                dlg.show();;
                dlg.center();
        }
        
}
