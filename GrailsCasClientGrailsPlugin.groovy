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
'''

    def documentation = "http://grails.org/plugin/grails-cas-client"

    def license = "APACHE"

    def issueManagement = [ system: "GITHUB", url: "https://github.com/cwang/grails-cas-client/issues" ]

    def scm = [ url: "https://github.com/cwang/grails-cas-client.git" ]

    def doWithWebDescriptor = { xml ->

        log.info('====== started adding JA-SIG CAS client support')

        def config = application.config.cas

        if (config.disabled) {
            log.info('the plugin is disabled therefore nothing needs to be done here.')
            return
        }

        boolean failed = false

        // to check configurations for filter and its mapping.
        if (config.loginUrl instanceof ConfigObject
                || config.validateUrl instanceof ConfigObject
                || config.urlPattern instanceof ConfigObject) {
            log.error('Please make sure that required parameters [cas.loginUrl, cas.validateUrl, cas.urlPattern] are set up correctly in Config.groovy of your application!')
//            System.exit(1)
            failed = true
        }
        else if (config.serverName instanceof ConfigObject && config.serviceUrl instanceof ConfigObject) {
            log.error('Please make sure that one of required parameters [cas.serverName, cas.serviceUrl] is set up correctly in Config.groovy of your application!')
//            System.exit(1)
            failed = true
        }
        else {
            log.info('checked configurations in Config.groovy')

            // to define name of the filter.
            String fname = 'CAS-Filter'

            // to add cas filter.
            def filters = xml.'filter'

            filters[0] + {
                'filter' {
                    'filter-name' (fname)
                    'filter-class' ('edu.yale.its.tp.cas.client.filter.CASFilter')
                    'init-param' {
                        'param-name' ('edu.yale.its.tp.cas.client.filter.loginUrl')
                        'param-value' (config.loginUrl)
                    }
                    'init-param' {
                        'param-name' ('edu.yale.its.tp.cas.client.filter.validateUrl')
                        'param-value' (config.validateUrl)
                    }

                    if (config.serverName instanceof CharSequence) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.serverName')
                            'param-value' (config.serverName)
                        }
                    }
                    else if (config.serviceUrl instanceof CharSequence) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.serviceUrl')
                            'param-value' (config.serviceUrl)
                        }
                    }

                    if (config.proxyCallbackUrl instanceof CharSequence) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.proxyCallbackUrl')
                            'param-value' (config.proxyCallbackUrl)
                        }
                    }
                    if (config.authorizedProxy instanceof CharSequence) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.authorizedProxy')
                            'param-value' (config.authorizedProxy)
                        }
                    }
                    if (config.renew instanceof Boolean) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.renew')
                            'param-value' (config.renew)
                        }
                    }
                    if (config.redirect instanceof Boolean) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.redirect')
                            'param-value' (config.redirect)
                        }
                    }
                    if (config.wrapRequest instanceof Boolean) {
                        'init-param' {
                            'param-name' ('edu.yale.its.tp.cas.client.filter.wrapRequest')
                            'param-value' (config.wrapRequest)
                        }
                    }
                }
            }

            log.info('added <filter/> section in web.xml')

            // to add cas filter mapping.
            def filtermappings = xml.'filter-mapping'

            if (config.urlPattern instanceof CharSequence) {

                filtermappings[0] + {
                    'filter-mapping' {
                        'filter-name' (fname)
                        'url-pattern' (config.urlPattern)
                    }
                }
                log.info('added <filter-mapping/> section(s) in web.xml')
            }
            else if (config.urlPattern instanceof List) {

                config.urlPattern.each { u ->
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
//                     System.exit(1)
                failed = true
            }
        }

        if (failed) {
            log.error("PLEASE CORRECT THE ERROR ABOVE!")
        }

        if (config.mocking) {
            log.info('/cas?u=USERNAME is available for mocking cas-ified user session')
            log.warn('Please take extra care as mocking should NOT be allowed for production environment!')
        }

        log.info('====== finished adding JA-SIG CAS client support')
    }
}
