package codepot.vendingmachine.domain;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CoinBank {

    private final List<Coin> coins = new ArrayList<>();

    public CoinBank() {
        this(
                Coin.PENNY, Coin.PENNY, Coin.PENNY, Coin.PENNY, Coin.PENNY, Coin.PENNY, Coin.PENNY,
                Coin.NICKEL, Coin.NICKEL, Coin.NICKEL, Coin.NICKEL, Coin.NICKEL, Coin.NICKEL, Coin.NICKEL,
                Coin.DIME, Coin.DIME, Coin.DIME, Coin.DIME, Coin.DIME, Coin.DIME, Coin.DIME,
                Coin.QUARTER, Coin.QUARTER, Coin.QUARTER, Coin.QUARTER, Coin.QUARTER, Coin.QUARTER, Coin.QUARTER
        );
    }

    public CoinBank(Coin... coins) {
        this.coins.addAll(Lists.newArrayList(coins));
    }

    public Collection<Coin> change(Transaction transaction) {
        List<Coin> changeCoins = new ArrayList<>();

        while (!(transaction.isZero() || coins.isEmpty())) {
            findMaxButLessThan(transaction.getValue()).ifPresent(c -> {
                transaction.reduce(c.getMoney());
                changeCoins.add(c);
            });
        }

        return changeCoins;
    }

    private Optional<Coin> findMaxButLessThan(Money balance) {
        List<Coin> copyOfCoins = Lists.newArrayList(coins);
        copyOfCoins.sort((o1, o2) -> -1 * o1.getValue().compareTo(o2.getValue()));

        for (Coin coinFromTreasure : copyOfCoins) {
            if (balance.greaterOrEquals(coinFromTreasure.getMoney())) {
                coins.remove(coinFromTreasure);
                return Optional.of(coinFromTreasure);
            }
        }
        return Optional.empty();
    }
}