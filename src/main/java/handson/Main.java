package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.orders.Order;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.Commands.*;
import static handson.Project.*;
import static handson.Utils.createSphereClient;

public class Main {

    private static final Address address = AddressBuilder.of(CountryCode.DE).build();

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        try (final BlockingSphereClient client = createSphereClient()){

            setUpProject(client);

            final Order order = onlineShop(client);

            // .. the order in a microservice, e.g. send confirmation email to the customer.

            cleanUpProject(client);
        }
    }

    /**
     * Online ship side:
     * - queries a cart, or creates one if none is available
     * - add product to cart
     * - sets a shipping address to cart
     * - creates order from cart
     * @param client CTP client
     * @return The created order
     */
    private static Order onlineShop(final BlockingSphereClient client){
        cart = queryFirstCart(client);
        System.out.println("[POST] Cart with id " + cart.getId() + " is queried/created");

        cart = addProductToCart(client, product.getId(), cart, 1L);
        System.out.println("[POST] Product with id " + product.getId() + " is added to cart.");

        cart = setShippingAddress(client, address, cart);
        System.out.println("[POST] Set address to cart with id " + cart.getId());

        // Checkout process continues...

        // When it is confirmed and paid, we create the order
        Order order = createOrderFromCart(client, cart);
        System.out.println("[POST] Order with id " + order.getId() + " is added to cart with id " + cart.getId());

        return order;
    }
}