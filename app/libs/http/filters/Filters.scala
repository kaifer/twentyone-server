package libs.http.filters

import javax.inject.Inject
/**
 * Created by kaifer on 2016. 6. 5..
 */
import play.api.http.HttpFilters
import play.filters.cors.CORSFilter


class Filters @Inject() (accessLoggingFilter: AccessLoggingFilter, corsFilter: CORSFilter) extends HttpFilters {
  val filters = Seq(accessLoggingFilter, corsFilter)
}