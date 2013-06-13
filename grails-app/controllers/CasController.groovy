

class CasController {

	def index = {
		if (org.codehaus.groovy.grails.commons.ConfigurationHolder.config.cas.mocking) {
			def model = [:]
			if (params.u) {
				session?.setAttribute(edu.yale.its.tp.cas.client.filter.CASFilter.CAS_FILTER_USER, params.u)
				model = [message: "Current cas-ified user is [${params.u}].", result: true]
			}
			else {
				model = [message: "Please supply a parameter 'u'!", result: false]
			}
			model
		}
		else {
			response.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND)
		}
	}
}
