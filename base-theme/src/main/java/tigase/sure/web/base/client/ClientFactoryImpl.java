/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XmppSessionLogic.SessionListener;
import tigase.jaxmpp.core.client.connector.ConnectorWrapper;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public abstract class ClientFactoryImpl implements ClientFactory {

        private final Theme theme = GWT.create(Theme.class);
        private final Jaxmpp jaxmpp = new Jaxmpp();
        
        private final EventBus eventBus = GWT.create(SimpleEventBus.class);
        
        private final BaseI18n baseI18n = GWT.create(BaseI18n.class);

        public ClientFactoryImpl() {
                theme().verticalTabPanelStyles().ensureInjected();
        }
        
		@Override
        public EventBus eventBus() {
                return eventBus;
        }

		@Override
        public Theme theme() {
                return theme;
        }

		@Override
        public Jaxmpp jaxmpp() {
                return jaxmpp;
        }
		
		@Override
		public SessionObject sessionObject() {
				return jaxmpp.getSessionObject();
		}
        
		@Override
        public BaseI18n baseI18n() {
                return baseI18n;
        }
}
