package im.adamant.android.interactors.wallets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class EthereumWalletFacade implements WalletFacade {
    @Override
    public BigDecimal getBalance() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAddress() {
        return "Coming soon";
    }

    @Override
    public SupportedWalletFacadeType getCurrencyType() {
        return SupportedWalletFacadeType.ETH;
    }

    @Override
    public String getTitle() {
        return "ETHEREUM WALLET";
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getBackgroundLogoResource() {
        return R.drawable.ic_ethereum_line;
    }

    @Override
    public void setChatStorage(ChatsStorage chatStorage) {

    }

    @Override
    public Single<List<CurrencyTransferEntity>> getLastTransfers() {
        return Single.just(new ArrayList<>());
    }

    @Override
    public boolean isAvailableAirdropLink() {
        return false;
    }

    @Override
    public int getAirdropLinkResource() {
        return 0;
    }

    @Override
    public String getAirdropLinkString() {
        return "";
    }
}