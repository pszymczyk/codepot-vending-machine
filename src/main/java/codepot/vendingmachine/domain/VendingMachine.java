package codepot.vendingmachine.domain;

import codepot.vendingmachine.infrastructure.notifiers.JiraServiceNotifier;
import codepot.vendingmachine.infrastructure.notifiers.MailServiceNotifier;
import codepot.vendingmachine.infrastructure.notifiers.SmsServiceNotifier;
import com.google.common.annotations.VisibleForTesting;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class VendingMachine {

    private final Set<Product> productsTray = new HashSet<>();
    private final Set<Coin> coinReturnTray = new HashSet<>();

    private final TransactionFactory transactionFactory;
    private final CoinBank coinBank;
    private final ProductStorage productStorage;
    private Optional<Transaction> currentTransaction = Optional.empty();

    public VendingMachine(TransactionFactory transactionFactory, CoinBank coinBank, ProductStorage productStorage) {
        this.transactionFactory = transactionFactory;
        this.coinBank = coinBank;
        this.productStorage = productStorage;
    }

    public String getDisplay() {
        return "INSERT A COIN";
    }


    public void selectProduct(String productCode) {
        currentTransaction.ifPresent(t -> {
                    Money productPrice = productStorage.getProductPrice(productCode);

                    if (t.getValue().greaterOrEquals(productPrice) && productStorage.isProductAvailable(productCode)) {
                        t.reduce(productPrice);
                        productsTray.add(productStorage.getProduct(productCode));
                        coinReturnTray.addAll(coinBank.change(t));
                        currentTransaction = Optional.empty();
                    }
                }
        );
    }

    public void insertCoin(Coin coin) {
        if (!currentTransaction.isPresent()) {
            currentTransaction = Optional.of(transactionFactory.createTransaction());
        }

        currentTransaction.get().add(coin);
    }

    public void cancel() {
        currentTransaction.ifPresent(t -> {
            coinReturnTray.addAll(coinBank.change(t));
        });

        currentTransaction = Optional.empty();
    }

    public Set<Coin> getCoinReturnTray() {
        return coinReturnTray;
    }

    public Set<Product> getProductsTray() {
        return productsTray;
    }


    @VisibleForTesting
    public Optional<Transaction> getCurrentTransaction() {
        return currentTransaction;
    }

    public static class Builder {

        private MutablePicoContainer pico;

        public Builder() {
            this(Optional.empty());
        }

        public Builder(Optional<PicoContainer> picoContainer) {
            pico = picoContainer.isPresent() ? new DefaultPicoContainer(picoContainer.get()) : new DefaultPicoContainer();

            pico.as(Characteristics.CACHE).addComponent(JiraServiceNotifier.class, JiraServiceNotifier.class);
            pico.as(Characteristics.CACHE).addComponent(SmsServiceNotifier.class, SmsServiceNotifier.class);
            pico.as(Characteristics.CACHE).addComponent(MailServiceNotifier.class, MailServiceNotifier.class);
            pico.as(Characteristics.CACHE).addComponent(ProductStorageNotifiers.class);

            if (pico.getComponent(ProductStorage.class) == null) {
                pico.as(Characteristics.CACHE).addComponent(ProductStorage.class, ProductStorage.class);
            }

            if (pico.getComponent(CoinBank.class) == null) {
                pico.as(Characteristics.CACHE).addComponent(CoinBank.class);
            }

            pico.addComponent(TransactionFactory.class, new PicoContainerTransactionFactory(pico));
            pico.as(Characteristics.CACHE).addComponent(VendingMachine.class);
        }



        public VendingMachine build() {
            return pico.getComponent(VendingMachine.class);
        }
    }
}
