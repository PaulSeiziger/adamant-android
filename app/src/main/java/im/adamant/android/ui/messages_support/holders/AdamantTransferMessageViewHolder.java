package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.ui.TransferDetailsScreen;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import ru.terrakok.cicerone.Router;

public class AdamantTransferMessageViewHolder extends AbstractMessageViewHolder {
    private TextView amountView;
    private View contentView;
    private Router router;

    public AdamantTransferMessageViewHolder(Router router, Context context, View v, AdamantMarkdownProcessor adamantAddressProcessor, Avatar avatar) {
        super(context, v, adamantAddressProcessor, avatar);
        this.router = router;

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.list_subitem_adamant_transfer_message, contentBlock, false);
        contentBlock.addView(contentView);
        amountView = contentView.findViewById(R.id.list_item_message_amount);
    }

    @Override
    public void bind(MessageListContent message, boolean isNextMessageWithSameSender, boolean isLastMessage) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() != SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE);

        if (isCorruptedMessage) {
            emptyView();
            return;
        }

        super.bind(message, isNextMessageWithSameSender, isLastMessage);

        AdamantTransferMessage adamantTransferMessage = (AdamantTransferMessage) message;

        View.OnClickListener onClickListener = v -> {
            Bundle bundle = new Bundle();
            bundle.putString(TransferDetailsScreen.ARG_TRANSFER_ID_KEY, adamantTransferMessage.getTransactionId());
            bundle.putString(TransferDetailsScreen.ARG_CURRENCY_ABBR, SupportedWalletFacadeType.ADM.toString());
            router.navigateTo(Screens.TRANSFER_DETAILS_SCREEN, bundle);
        };
        amountView.setOnClickListener(onClickListener);

        String amountText = String.format(Locale.ENGLISH, "%.3f", adamantTransferMessage.getAmount()) + " " +
                context.getResources().getString(R.string.adm_currency_abbr);

        amountView.setText(amountText);

        displayProcessedStatus(adamantTransferMessage);
    }
}
