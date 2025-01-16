Content(parts=[Part(text=```kotlin
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class OrderStateMachineTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var paymentService: PaymentService
    private lateinit var notificationService: NotificationService
    private lateinit var orderStateMachine: OrderStateMachine

    @Before
    fun setup() {
        orderRepository = mock()
        paymentService = mock()
        notificationService = mock()
        orderStateMachine = OrderStateMachine(orderRepository, paymentService, notificationService)
    }

    @Test
    fun `handleEvent confirms created order`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Created)
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Confirmed)).thenReturn(OrderStateMachine.OrderState.Confirmed)

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.Confirm)

        assertEquals(OrderStateMachine.OrderState.Confirmed, newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Confirmed)
        verify(notificationService).notify("Order $orderId state changed from Created to Confirmed")
    }

    @Test
    fun `handleEvent fails created order`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Created)
        val reason = "Invalid item"
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Failed(reason))).thenReturn(OrderStateMachine.OrderState.Failed(reason))

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.Fail(reason))

        assertEquals(OrderStateMachine.OrderState.Failed(reason), newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Failed(reason))
        verify(notificationService).notify("Order $orderId state changed from Created to Failed(reason=$reason)")
    }

    @Test
    fun `handleEvent processes payment in confirmed state successfully`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Confirmed)
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(paymentService.processPayment()).thenReturn(PaymentResult.Success)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Paid)).thenReturn(OrderStateMachine.OrderState.Paid)

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.ProcessPayment)

        assertEquals(OrderStateMachine.OrderState.Paid, newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Paid)
        verify(notificationService).notify("Order $orderId state changed from Confirmed to Paid")
    }

    @Test
    fun `handleEvent processes payment in confirmed state unsuccessfully`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Confirmed)
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(paymentService.processPayment()).thenReturn(PaymentResult.Failure)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.PaymentPending(1))).thenReturn(OrderStateMachine.OrderState.PaymentPending(1))

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.ProcessPayment)

        assertEquals(OrderStateMachine.OrderState.PaymentPending(1), newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.PaymentPending(1))
        verify(notificationService).notify("Order $orderId state changed from Confirmed to PaymentPending(attempts=1)")
    }

    @Test
    fun `handleEvent processes payment in PaymentPending state successfully after multiple failures`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.PaymentPending(2))
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(paymentService.processPayment()).thenReturn(PaymentResult.Success)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Paid)).thenReturn(OrderStateMachine.OrderState.Paid)

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.ProcessPayment)

        assertEquals(OrderStateMachine.OrderState.Paid, newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Paid)
        verify(notificationService).notify("Order $orderId state changed from PaymentPending(attempts=2) to Paid")
    }

    @Test
    fun `handleEvent fails payment after max attempts`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.PaymentPending(3))
        val reason = "Max payment attempts exceeded"
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Failed(reason))).thenReturn(OrderStateMachine.OrderState.Failed(reason))

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.ProcessPayment)

        assertEquals(OrderStateMachine.OrderState.Failed(reason), newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Failed(reason))
        verify(notificationService).notify("Order $orderId state changed from PaymentPending(attempts=3) to Failed(reason=$reason)")
    }


    @Test
    fun `handleEvent ships paid order`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Paid)
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)
        whenever(orderRepository.updateOrderState(orderId, OrderStateMachine.OrderState.Shipped)).thenReturn(OrderStateMachine.OrderState.Shipped)

        val newState = orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.Ship)

        assertEquals(OrderStateMachine.OrderState.Shipped, newState)
        verify(orderRepository).updateOrderState(orderId, OrderStateMachine.OrderState.Shipped)
        verify(notificationService).notify("Order $orderId state changed from Paid to Shipped")
    }

    @Test
    fun `handleEvent throws exception for invalid event`() = runBlocking {
        val orderId = "123"
        val order = Order(orderId, OrderStateMachine.OrderState.Created)
        whenever(orderRepository.getOrder(orderId)).thenReturn(order)

        assertFailsWith<IllegalStateException> {
            orderStateMachine.handleEvent(orderId, OrderStateMachine.OrderEvent.Ship)
        }
    }


    //Helper classes for mocking and testing

    data class Order(val id: String, val state: OrderStateMachine.OrderState)

    interface OrderRepository {
        suspend fun getOrder(orderId: String): Order
        suspend fun updateOrderState(orderId: String, newState: OrderStateMachine.OrderState): OrderStateMachine.OrderState
    }

    sealed class PaymentResult {
        object Success : PaymentResult()
        object Failure : PaymentResult()
    }

    interface PaymentService {
        suspend fun processPayment(): PaymentResult
    }

    interface NotificationService {
        suspend fun notify(message: String)
    }
}
```)], role=model)