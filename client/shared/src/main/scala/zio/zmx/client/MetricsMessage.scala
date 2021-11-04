package zio.zmx.client

import zio.metrics._
import java.time.Instant

import upickle.default._
import zio.MetricLabel
import zio.Duration

sealed trait ClientMessage

object ClientMessage {
  def subscribe: ClientMessage = Subscribe

  case object Subscribe extends ClientMessage
}

sealed trait MetricsMessage {
  def key: MetricKey
  def when: Instant
}

object MetricsMessage {
  implicit private val rwInstant: ReadWriter[Instant]   =
    readwriter[Long].bimap(_.toEpochMilli(), Instant.ofEpochMilli(_))
  implicit private val rwDuration: ReadWriter[Duration] =
    readwriter[Long].bimap(_.toMillis(), Duration.fromMillis(_))

  implicit private val rwMetricLabel: ReadWriter[MetricLabel] = macroRW[MetricLabel]

  implicit private val rwGaugeKey: ReadWriter[MetricKey.Gauge]         = macroRW[MetricKey.Gauge]
  implicit private val rwCounterKey: ReadWriter[MetricKey.Counter]     = macroRW[MetricKey.Counter]
  implicit private val rwHistogramKey: ReadWriter[MetricKey.Histogram] = macroRW[MetricKey.Histogram]
  implicit private val rwSummaryKey: ReadWriter[MetricKey.Summary]     = macroRW[MetricKey.Summary]
  implicit private val rwSetCountKey: ReadWriter[MetricKey.SetCount]   = macroRW[MetricKey.SetCount]

  implicit private val rwMetricTypeCounter   = macroRW[MetricType.Counter]
  implicit private val rwMetricTypeGauge     = macroRW[MetricType.Gauge]
  implicit private val rwMetricTypeHistogram = macroRW[MetricType.DoubleHistogram]
  implicit private val rwMetricTypeSummary   = macroRW[MetricType.Summary]
  implicit private val rwMetricTypeSetCount  = macroRW[MetricType.SetCount]

  implicit private val rwMetricState: ReadWriter[MetricState] = macroRW[MetricState]
  implicit private val rwMetricType: ReadWriter[MetricType]   = macroRW[MetricType]

  implicit val rw = macroRW[MetricsMessage]

  final case class GaugeChange(key: MetricKey.Gauge, when: Instant, value: Double, delta: Double) extends MetricsMessage
  object GaugeChange   {
    implicit val rw: ReadWriter[GaugeChange] = macroRW[GaugeChange]
  }
  final case class CounterChange(key: MetricKey.Counter, when: Instant, absValue: Double, delta: Double)
      extends MetricsMessage
  object CounterChange {
    implicit val rw: ReadWriter[CounterChange] = macroRW[CounterChange]
  }

  final case class HistogramChange(key: MetricKey.Histogram, when: Instant, value: MetricState) extends MetricsMessage
  object HistogramChange {
    implicit val rw: ReadWriter[HistogramChange] = macroRW[HistogramChange]
  }
  final case class SummaryChange(key: MetricKey.Summary, when: Instant, value: MetricState) extends MetricsMessage
  object SummaryChange   {
    implicit val rw: ReadWriter[SummaryChange] = macroRW[SummaryChange]
  }
  final case class SetChange(key: MetricKey.SetCount, when: Instant, value: MetricState) extends MetricsMessage
  object SetChange       {
    implicit val rw: ReadWriter[SetChange] = macroRW[SetChange]
  }

}