class OrderStateMachine(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val notificationService: NotificationService
) {
    sealed class OrderState {
        object Created : OrderState()
        object Confirmed : OrderState()
        data class PaymentPending(val attempts: Int) : OrderState()
        object Paid : OrderState()
        object Shipped : OrderState()
        data class Failed(val reason: String) : OrderState()
    }

    sealed class OrderEvent {
        object Confirm : OrderEvent()
        object ProcessPayment : OrderEvent()
        object Ship : OrderEvent()
        data class Fail(val reason: String) : OrderEvent()
    }

    suspend fun handleEvent(orderId: String, event: OrderEvent): OrderState {
        val order = orderRepository.getOrder(orderId)
        val newState = when (val currentState = order.state) {
            is OrderState.Created -> handleCreatedState(event)
            is OrderState.Confirmed -> handleConfirmedState(event)
            is OrderState.PaymentPending -> handlePaymentPendingState(event, currentState)
            is OrderState.Paid -> handlePaidState(event)
            is OrderState.Shipped -> currentState
            is OrderState.Failed -> currentState
        }

        return orderRepository.updateOrderState(orderId, newState)
            .also { notifyStateChange(orderId, currentState, newState) }
    }

    private suspend fun handleCreatedState(event: OrderEvent): OrderState =
        when (event) {
            is OrderEvent.Confirm -> OrderState.Confirmed
            is OrderEvent.Fail -> OrderState.Failed(event.reason)
            else -> throw IllegalStateException("Invalid event for Created state")
        }

    private suspend fun handleConfirmedState(event: OrderEvent): OrderState =
        when (event) {
            is OrderEvent.ProcessPayment -> {
                val paymentResult = paymentService.processPayment()
                if (paymentResult.isSuccess) OrderState.Paid
                else OrderState.PaymentPending(attempts = 1)
            }
            is OrderEvent.Fail -> OrderState.Failed(event.reason)
            else -> throw IllegalStateException("Invalid event for Confirmed state")
        }

    private suspend fun handlePaymentPendingState(
        event: OrderEvent,
        currentState: OrderState.PaymentPending
    ): OrderState = when (event) {
        is OrderEvent.ProcessPayment -> {
            if (currentState.attempts >= 3) {
                OrderState.Failed("Max payment attempts exceeded")
            } else {
                val paymentResult = paymentService.processPayment()
                if (paymentResult.isSuccess) OrderState.Paid
                else OrderState.PaymentPending(currentState.attempts + 1)
            }
        }
        is OrderEvent.Fail -> OrderState.Failed(event.reason)
        else -> throw IllegalStateException("Invalid event for PaymentPending state")
    }

    private suspend fun handlePaidState(event: OrderEvent): OrderState =
        when (event) {
            is OrderEvent.Ship -> OrderState.Shipped
            is OrderEvent.Fail -> OrderState.Failed(event.reason)
            else -> throw IllegalStateException("Invalid event for Paid state")
        }

    private suspend fun notifyStateChange(
        orderId: String,
        oldState: OrderState,
        newState: OrderState
    ) {
        notificationService.notify(
            "Order $orderId state changed from $oldState to $newState"
        )
    }
}