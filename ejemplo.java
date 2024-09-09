import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioEjemplo {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Leer el archivo JSON de stock (Con su respectiva direccion)
        StockData stockData = mapper.readValue(new File("stock.json"), StockData.class);
		
        // Leer el archivo JSON de orders (Con su respectiva direccion)
        OrdersData ordersData = mapper.readValue(new File("orders.json"), OrdersData.class);
				

        // Crear un mapa para mantener los niveles de inventario por artículo
        Map<String, InventoryLevel> inventoryMap = new HashMap<>();

        // Inicializar inventario con las cantidades contables y bloqueadas
        for (Stock stock : stockData.getStock()) {
            inventoryMap.put(stock.getItem(), new InventoryLevel(stock.getCountable(), stock.getBlocked()));
        }

        // Procesar las órdenes y calcular las cantidades reservadas (booked) y las faltantes (missing)
        for (Order order : ordersData.getOrders()) {
            if (order.getStatus().equals("not fulfilled")) {
                for (OrderLine line : order.getOrderLines()) {
                    InventoryLevel level = inventoryMap.get(line.getItem());
                    if (level != null) {
                        level.booked += line.getQuantity();
						
						// un arreglo que lleve cuentas de almacen, comprometridos, podridos y al final incluirlos en las sumas generales del mismo item

                        // Calcular la cantidad faltante si no hay suficiente stock
                        if (line.getQuantity() > level.getAvailable()) {
                            level.missing += line.getQuantity() - level.getAvailable();
                        }
                    }
                }
            }
        }

        // Mostrar los niveles de inventario
        for (Map.Entry<String, InventoryLevel> entry : inventoryMap.entrySet()) {
            String item = entry.getKey();
            InventoryLevel level = entry.getValue();
            System.out.println("Item: " + item);
            System.out.println("Count: " + level.countable);
            System.out.println("Blocked: " + level.blocked);
            System.out.println("Booked: " + level.booked);
            System.out.println("Missing: " + level.missing);
            System.out.println("Available: " + level.getAvailable());
            System.out.println();
        }
    }
}

class StockData {
    private List<Stock> stock;

    public List<Stock> getStock() {
        return stock;
    }

    public void setStock(List<Stock> stock) {
        this.stock = stock;
    }
}

class Stock {
    private String item;
    private int countable;
    private int blocked;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getCountable() {
        return countable;
    }

    public void setCountable(int countable) {
        this.countable = countable;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }
}

class OrdersData {
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

class Order {
    private int orderId;
    private String status;
    private List<OrderLine> orderLines;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}

class OrderLine {
    private String item;
    private int quantity;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

class InventoryLevel {
    int countable;
    int blocked;
    int booked;
    int missing;

    public InventoryLevel(int countable, int blocked) {
        this.countable = countable;
        this.blocked = blocked;
        this.booked = 0;
        this.missing = 0;
    }

    public int getAvailable() {
        return countable - blocked - booked;
    }
}
