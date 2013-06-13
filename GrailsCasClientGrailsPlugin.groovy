import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class GrailsCasClientGrailsPlugin {

    def version = "2.0"
    def grailsVersion = "1.0 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Grails CAS Client Plugin" // Headline display name of the plugin
    def author = "Chen Wang"
    def authorEmail = "dev@chenwang.org"
    def description = '''\
The plugin handles configurations of JA-SIG CAS client integration using Yale Java client library versioned 2.1.1 with
some added features. Please note there is another Java client library which is heavily Spring based; Although it seems
a natural fit for Grails applications, it requires more configuration works to get started.

Please make sure necessary configurations are made in your Grails application's Config.groovy file.
'''

    def documentation = "http://grails.org/plugin/grails-cas-client"

    def license = "APACHE"

    def issueManagement = [ system: "GITHUB", url: "https://github.com/cwang/grails-cas-client/issues" ]

    def scm = [ url: "https://github.com/cwang/grails-cas-client.git" ]

    def doWithWebDescriptor = { xml ->
		log.info('====== started adding JA-SIG CAS client support')

        if (ConfigurationHolder.config.cas.disabled) {
            log.info('the plugin is disabled therefore nothing needs to be done here.')
        }
        else {

            def failed = false

            // to check configurations for filter and its mapping.
            if (ConfigurationHolder.config.cas.loginUrl instanceof ConfigObject
                    || ConfigurationHolder.config.cas.validateUrl instanceof ConfigObject
                    || ConfigurationHolder.config.cas.urlPattern instanceof ConfigObject) {
                log.error('Please make sure that required parameters [cas.loginUrl, cas.validateUrl, cas.urlPattern] are set up correctly in Config.groovy of your application!')
//			 	System.exit(1)
                failed = true
            }
            else if (ConfigurationHolder.config.cas.serverName instanceof ConfigObject && ConfigurationHolder.config.cas.serviceUrl instanceof ConfigObject) {
                log.error('Please make sure that one of required parameters [cas.serverName, cas.serviceUrl] is set up correctly in Config.groovy of your application!')
//			    System.exit(1)
                failed = true
            }
            else {
			    log.info('checked configurations in Config.groovy')

                // to define name of the filter.
                def fname = 'CAS-Filter'

                // to add cas filter.
                def filters = xml.'filter'

                filters[0] + {
                    'filter' {
                        'filter-name' (fname)
                        'filter-class' ('edu.yale.its.tp.cas.client.filter.CASFilter')
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.loginUrl')
                            'param-value' ("${ConfigurationHolder.config.cas.loginUrl}")
                        }
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.validateUrl')
                            'param-value' ("${ConfigurationHolder.config.cas.validateUrl}")
                        }

                        if (ConfigurationHolder.config.cas.serverName instanceof String) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.serverName')
                                'param-value' ("${ConfigurationHolder.config.cas.serverName}")
                            }
                        }
                        else if (ConfigurationHolder.config.cas.serviceUrl instanceof String) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.serviceUrl')
                                'param-value' ("${ConfigurationHolder.config.cas.serviceUrl}")
                            }
                        }

                        if (ConfigurationHolder.config.cas.proxyCallbackUrl instanceof String) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.proxyCallbackUrl')
                                'param-value' ("${ConfigurationHolder.config.cas.proxyCallbackUrl}")
                            }
                        }
                        if (ConfigurationHolder.config.cas.authorizedProxy instanceof String) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.authorizedProxy')
                                'param-value' ("${ConfigurationHolder.config.cas.authorizedProxy}")
                            }
                        }
                        if (ConfigurationHolder.config.cas.renew instanceof Boolean) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.renew')
                                'param-value' ("${ConfigurationHolder.config.cas.renew}")
                            }
                        }
                        if (ConfigurationHolder.config.cas.redirect instanceof Boolean) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.redirect')
                                'param-value' ("${ConfigurationHolder.config.cas.redirect}")
                            }
                        }
                        if (ConfigurationHolder.config.cas.wrapRequest instanceof Boolean) {
                            'init-param' {
                                'param-name' ('edu.yale.its.tp.cas.client.filter.wrapRequest')
                                'param-value' ("${ConfigurationHolder.config.cas.wrapRequest}")
                            }
                        }
                    }
                }

                log.info('added <filter/> section in web.xml')


                // to add cas filter mapping.
                def filtermappings = xml.'filter-mapping'


                if (ConfigurationHolder.config.cas.urlPattern instanceof String) {

                    filtermappings[0] + {
                        'filter-mapping' {
                            'filter-name' (fname)
                            'url-pattern' ("${ConfigurationHolder.config.cas.urlPattern}")
                        }
                    }
                    log.info('added <filter-mapping/> section(s) in web.xml')
                }
                else if (ConfigurationHolder.config.cas.urlPattern instanceof java.util.List) {

                    ConfigurationHolder.config.cas.urlPattern.each { u ->
                        filtermappings[0] + {
                            'filter-mapping' {
                                'filter-name' (fname)
                                'url-pattern' ("${u}")
                            }
                        }
                    }
                    log.info('added <filter-mapping/> section(s) in web.xml')
                }
                else {
                    log.error('Please make sure that required parameter [cas.urlPattern] is an instance of either java.lang.String or java.util.List in Config.groovy of your application!')
//			 		System.exit(1)
                    failed = true
                }

            }

            if (failed) {
                log.error("PLEASE CORRECT THE ERROR ABOVE!")
            }

        }

        if (ConfigurationHolder.config.cas.mocking) {
            log.info('/cas?u=USERNAME is available for mocking cas-ified user session')
            log.warn('Please take extra care as mocking should NOT be allowed for production environment!')
        }

		log.info('====== finished adding JA-SIG CAS client support')
    }

    def doWithSpring = {
        // nothing to do with spring here.
    }

    def doWithDynamicMethods = { ctx ->
        // nothing to do with dynamic methods here.
    }

    def doWithApplicationContext = { applicationContext ->
        // nothing to do with application context here.
    }

    def onChange = { event ->
        // no interests in dynamic loading.
    }

    def onConfigChange = { event ->
        // no interests in dynamic loading.
    }

    def onShutdown = { event ->
    }
}
