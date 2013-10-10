import grails.util.GrailsUtil

class GrailsCasClientGrailsPlugin {

    def version = "2.0"
    def grailsVersion = "1.0 > *"

    def title = "Grails CAS Client Plugin"
    def author = "Chen Wang"
    def authorEmail = "dev@chenwang.org"
    def description = '''\
The plugin handles configurations of JA-SIG CAS client integration using Yale Java client library versioned 2.1.1 with
some added features. Please note there is another Java client library which is heavily Spring based; Although it seems
a natural fit for Grails applications, it requires more configuration works to get started.

Please make sure necessary configurations are made in your Grails application's Config.groovy file.
in config.groovy add following config
//for jasig-sis cas
grails.cas.disabled = false
grails.cas.casServerLogoutUrl   = 'http://10.67.10.52:8989/sso/logout'
grails.cas.casServerLoginUrl    = 'http://10.67.10.52:8989/sso/login'
grails.cas.serverName           = 'http://10.66.30.249:8090'
grails.cas.casServerUrlPrefix   = 'http://10.67.10.52:8989/sso'
grails.cas.urlPattern           = '/ssologin' //this is for the sso login entry


'''

    def documentation = "http://grails.org/plugin/grails-cas-client"

    def license = "APACHE"

    def issueManagement = [ system: "GITHUB", url: "https://github.com/cwang/grails-cas-client/issues" ]

    def scm = [ url: "https://github.com/cwang/grails-cas-client.git" ]


    def doWithWebDescriptor = { xml ->
//		System.out.println('====== started adding JA-SIG CAS client support')
        def config = application.config.grails.cas

        if (config.cas.disabled) {
            System.out.println('CAS CLIENT PLUGIN INFO: the plugin is disabled therefore nothing needs to be done here.')
        }
        else {
            //to add context-param
            def contextParam = xml.'context-param'

            contextParam[0] + {
                'context-param'{
                    'param-name'('casServerLogoutUrl')
                    'param-value'(config.casServerLogoutUrl)
                }
            }

            def listenerParam = xml.'listener'
            listenerParam[0] + {
                'listener'{
                    'listener-class'('org.jasig.cas.client.session.SingleSignOutHttpSessionListener')
                }
            }


            // to add cas filter.
            def filters = xml.'filter'

            filters[0] + {
                'filter' {
                    'filter-name'('CAS Single Sign Out Filter')
                    'filter-class'('org.jasig.cas.client.session.SingleSignOutFilter')
                }
                'filter' {
                    'filter-name'('CASFilter')
                    'filter-class'('org.jasig.cas.client.authentication.AuthenticationFilter')
                    'init-param'{
                        'param-name'('casServerLoginUrl')
                        'param-value'(config.casServerLoginUrl)
                    }
                    'init-param'{
                        'param-name'('serverName')
                        'param-value'(config.serverName)
                    }
                }
                'filter' {
                    'filter-name'('CAS Validation Filter')
                    'filter-class'('org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter')
                    'init-param'{
                        'param-name'('casServerUrlPrefix')
                        'param-value'(config.casServerUrlPrefix)
                    }
                    'init-param'{
                        'param-name'('serverName')
                        'param-value'(config.serverName)
                    }
                }
                'filter' {
                    'filter-name'('CAS HttpServletRequest Wrapper Filter')
                    'filter-class'('org.jasig.cas.client.util.HttpServletRequestWrapperFilter')
                }
                'filter' {
                    'filter-name'('CAS Assertion Thread Local Filter')
                    'filter-class'('org.jasig.cas.client.util.AssertionThreadLocalFilter')
                }
            }

            System.out.println('CAS CLIENT PLUGIN INFO: added <filter/> section in web.xml')


            // to add cas filter mapping.
            def filtermappings = xml.'filter-mapping'

            filtermappings[0] + {
                'filter-mapping' {
                    'filter-name' ('CAS Single Sign Out Filter')
                    'url-pattern' ('/logout')
                }
                'filter-mapping' {
                    'filter-name' ('CASFilter')
                    'url-pattern' (config.urlPattern)
                }
                'filter-mapping' {
                    'filter-name' ('CAS Validation Filter')
                    'url-pattern' (config.urlPattern)
                }
                'filter-mapping' {
                    'filter-name' ('CAS HttpServletRequest Wrapper Filter')
                    'url-pattern' (config.urlPattern)
                }
                'filter-mapping' {
                    'filter-name' ('CAS Assertion Thread Local Filter')
                    'url-pattern' (config.urlPattern)
                }
            }
            System.out.println('CAS CLIENT PLUGIN INFO: added <filter-mapping/> section(s) in web.xml')


        }

    }
}
