package tigase.sure.web.site.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.ConnectionConfiguration;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;
import tigase.jaxmpp.gwt.client.connectors.WebSocket;
import tigase.jaxmpp.gwt.client.dns.WebDnsResolver;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.RootView;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthHandler;
import tigase.sure.web.base.client.auth.AuthRequestEvent;
import tigase.sure.web.base.client.auth.AuthRequestHandler;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatPlace;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Xode implements EntryPoint {

        private static final Logger log = Logger.getLogger("Xode");

        // need this for reconnect during reaction on see-other-host
        private JID jid = null;
        private String password = null;
        
        private ClientFactory factory;
        
		private DnsResult dnsResult = null;
		
		/**
         * This is the entry point method.
         */
        public void onModuleLoad() {

                factory = GWT.create(ClientFactory.class);
                factory.theme().style().ensureInjected();

                RootView view = new RootView(factory);

				
//                AbsolutePanel center = new AbsolutePanel();
//                center.add(new Label("Center panel"));

//                AppView appView = new AppView(center, factory);
//                ResizeLayoutPanel appView = new ResizeLayoutPanel();
//                view.setCenter(appView);
                XTest appView = new XTest();
                view.setCenter(appView);

                EventBus eventBus = factory.eventBus();
                final PlaceController placeController = factory.placeController();

                // Start ActivityManager for the main widget with our ActivityMapper
                ActivityMapper activityMapper = new AppActivityMapper(factory);
                ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
                activityManager.setDisplay(appView);

                Place defaultPlace = new AuthPlace();//new ChatPlace();

                // Start PlaceHistoryHandler with our PlaceHistoryMapper
                AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
                PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
                historyHandler.register(placeController, eventBus, defaultPlace);

                //appView.getActionBar().setSearchBox(new TextBox());

                RootLayoutPanel.get().add(view);

                //historyHandler.handleCurrentHistory();

                eventBus.addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {                                
                                if (factory.jaxmpp().getSessionObject().getProperty(SessionObject.USER_BARE_JID) != null) {
                                        placeController.goTo(new ChatPlace());
                                }
                                else {
                                        placeController.goTo(new AuthPlace());
                                }
                        }

                        public void deauthenticated(String msg, SaslModule.SaslError saslError) {
                                placeController.goTo(new AuthPlace());
                        }
                });
                eventBus.addHandler(AuthRequestEvent.TYPE, new AuthRequestHandler() {

                        public void authenticate(JID jid, String password, String boshUrl) {
                                authenticateInt(jid, password, boshUrl);
                        }
                });

                placeController.goTo(new AuthPlace());
                
                if (Cookies.getCookie("username") != null && Cookies.getCookie("password") != null) {
                        authenticateInt(JID.jidInstance(Cookies.getCookie("username")), Cookies.getCookie("password"), null);
                }
                //authenticateTest(factory);
                
                authenticateInt(null, null, null);
        }

        public void authenticateInt(final JID jid, final String password, String boshUrl) {                 
                // storing jid and password to use it during reconnection for see-other-host
                this.jid = jid;
                this.password = password;
                
				final Dictionary root = Dictionary.getDictionary("root");
				
				ConnectionConfiguration connCfg = factory.jaxmpp().getConnectionConfiguration();
				connCfg.setDomain(null);
				connCfg.setUserJID(jid == null ? null : jid.getBareJid());
				if (jid == null) {
					String domain = root.get("anon-domain");
					connCfg.setDomain(domain);
					if (boshUrl == null) {
						boshUrl = getBoshUrl(domain);
					}
				}
				if (boshUrl != null) {
					factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, boshUrl);
				} else {
					factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, null);
					String webDnsResolver = root.get("dns-resolver");
					factory.jaxmpp().getSessionObject().setUserProperty(WebDnsResolver.WEB_DNS_RESOLVER_URL_KEY, webDnsResolver);
				}
				connCfg.setUserPassword(password);
				try {
                        factory.jaxmpp().login();
                } catch (JaxmppException ex) {
                        log.log(Level.WARNING, "login exception", ex);
                        //log.log(Level.WARNING, "login exception", ex);
                }
        }
        
        public static String getBoshUrl(String domain) {
                Dictionary domains = Dictionary.getDictionary("domains");
                String url = "http://" + domain + ":5280/bosh";
                if (domains != null) {
                        Set<String> keys = domains.keySet();
                        if (keys.contains(domain)) {
                                url = domains.get(domain);
                        } else if (keys.contains("default")) {
                                url = domains.get("default");
                        }
                }
				if (WebSocket.isSupported()) {
					if (url.startsWith("http://")) {
						url = url.replace("http://", "ws://").replace(":5280", ":5290");
					}
				}
                return url;
        }
        
        private class XTest extends ResizeComposite implements ProvidesResize, AcceptsOneWidget {

                private final ResizablePanel panel;
                private IsWidget widget = null;
                
                public XTest() {
                        panel = new ResizablePanel();
                        
                        initWidget(panel);
                }                
                
                @Override
                public void onResize() {
                        super.onResize();
                        
                        if (widget != null && widget instanceof RequiresResize) {
                                ((RequiresResize) widget).onResize();
                        }
                }

                public void setWidget(IsWidget w) {
                        
                        if (widget != null) {
                                panel.remove(widget);
                        }
                        
                        widget = w;
                        
                        log.info("setting widget = " + w);
                        
                        if (w != null) {
                                panel.add(w);
                                
                                Style style = w.asWidget().getElement().getStyle();
                                style.setPosition(Style.Position.ABSOLUTE);
                                style.setLeft(0, Unit.EM);
                                style.setRight(0, Unit.EM);
                                style.setTop(0, Unit.EM);
                                style.setBottom(0, Unit.EM);
                                         
                                if (widget != null && widget instanceof RequiresResize) {
                                        ((RequiresResize) widget).onResize();
                                }
                        }
                }
                
        }
}