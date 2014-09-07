/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.base.client.ClientFactory;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;

/**
 *
 * @author andrzej
 */
public class AbstractAuthView extends ResizeComposite {
        
        protected final ClientFactory factory;
        
        private TextBox username;
        private TextBox password;
        private Button authButton;
        private DisclosurePanel disclosure;
        private TextBox boshUrl;
		private AbsolutePanel errorPanel;
        
        public AbstractAuthView(ClientFactory factory_) {
                this.factory = factory_;
                
                this.factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {
                                authFinished();
                        }

                        public void deauthenticated(String msg, SaslModule.SaslError saslError) {
                                authFinished();
                        }
                        
                });
        }

        private void authFinished() {
                authButton.setEnabled(true);
                authButton.addStyleName(factory.theme().style().buttonDefault());
                authButton.removeStyleName(factory.theme().style().buttonDisabled());                
        }
        
        private void handle() {
				errorPanel.setVisible(false);
                authButton.setEnabled(false);
                authButton.removeStyleName(factory.theme().style().buttonDefault());
                authButton.addStyleName(factory.theme().style().buttonDisabled());                
                String url = (boshUrl != null) ? boshUrl.getText() : null;
                if (url != null) {
                        url = url.trim();
                        if (url.isEmpty()) {
                                url = null;
                        }
                }
                factory.eventBus().fireEvent(new AuthRequestEvent(JID.jidInstance(username.getText()), password.getText(), url));
        }
        
        protected AbsolutePanel createAuthBox(boolean advanced) {
                AbsolutePanel panel = new AbsolutePanel();

                Label header = new Label(factory.baseI18n().authenticate());
                header.setStyleName(factory.theme().style().authHeader());
                panel.add(header);
                
                Label jidLabel = new Label(factory.baseI18n().jid());
                jidLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(jidLabel);
                
                username = new TextBox();
                username.setStyleName(factory.theme().style().authTextBox());
                panel.add(username);
                
                Label passwordLabel = new Label(factory.baseI18n().password());
                passwordLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(passwordLabel);
                
                password = new PasswordTextBox();
                password.setStyleName(factory.theme().style().authTextBox());
                panel.add(password);
                
                if (advanced) {
                        disclosure = new DisclosurePanel(factory.baseI18n().advanced());

                        Label boshUrlLabel = new Label(factory.baseI18n().boshUrl() + ":");
                        FlowPanel disclosurePanel = new FlowPanel();
                        disclosurePanel.add(boshUrlLabel);
                        boshUrl = new TextBox();
                        boshUrl.setStyleName(factory.theme().style().authTextBox());
                        boshUrl.getElement().getStyle().setWidth(97, Style.Unit.PCT);
                        disclosurePanel.add(boshUrl);

                        disclosure.add(disclosurePanel);
                        disclosure.getElement().getStyle().setWidth(100, Style.Unit.PCT);

                        panel.add(disclosure);
                }
                
				errorPanel = new AbsolutePanel();
				errorPanel.setStyleName(factory.theme().style().errorPanelStyle());
				errorPanel.setVisible(false);
				panel.add(errorPanel);
				
				factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {
					public void authenticated(JID jid) {
						errorPanel.setVisible(false);
					}

					public void deauthenticated(String msg, SaslModule.SaslError saslError) {
						if (msg != null) {
							errorPanel.getElement().setInnerText(msg);
						}
						if (saslError != null) {
							errorPanel.getElement().setInnerText("Authentication error: " + saslError.name());
						}
						if (msg != null || saslError != null) {
							errorPanel.setVisible(true);
						}
					}
				});
				
                authButton = new Button(factory.baseI18n().authenticate());
                authButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                                handle();
                        }                        
                });
                authButton.setStyleName(factory.theme().style().authButton());
                panel.add(authButton);
                
                panel.setStyleName(factory.theme().style().authPanel());
               
                KeyUpHandler handler = new KeyUpHandler() {

                        public void onKeyUp(KeyUpEvent event) {
                                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                                        authButton.click();
                                }
                        }
                        
                };
                
                username.addKeyUpHandler(handler);
                password.addKeyUpHandler(handler);
                
                return panel;
        }
        
}