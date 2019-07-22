package modules.AdvMessage;

import com.vdurmont.emoji.Emoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class ReactionListener {

    private Emoji emoji;
    private IMessage message;
    private IUser reactor;

    public ReactionListener(Emoji emoji, IMessage message, IUser reactor) {
        this.emoji = emoji;
        this.message = message;
        this.reactor = reactor;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public IMessage getMessage() {
        return message;
    }

    public IUser getReactor() {
        return reactor;
    }

    public void cancel() {

    }
}
