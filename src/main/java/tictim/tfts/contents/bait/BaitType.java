package tictim.tfts.contents.bait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class BaitType{
	private static final Pattern NAME_REGEX = Pattern.compile("[0-9a-z._-]+");

	public static final Codec<BaitType> CODEC = Codec.STRING.comapFlatMap(s -> {
		String[] err = new String[1];
		BaitType baitType = tryParse(s, e -> err[0] = e);
		return baitType!=null ? DataResult.success(baitType) : DataResult.error(() -> errMsg(s, err[0]));
	}, BaitType::toString);

	private static final Map<String, BaitType> cache = new Object2ObjectOpenHashMap<>();
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();

	@NotNull public static BaitType of(@NotNull String string){
		String[] err = new String[1];
		BaitType type = tryParse(string, e -> err[0] = e);
		if(type==null) throw new RuntimeException(errMsg(string, err[0]));
		return type;
	}

	private static String errMsg(@NotNull String string, @Nullable String errorMessage){
		StringBuilder stb = new StringBuilder();
		stb.append("Cannot parse bait type '").append(string).append('\'');
		if(errorMessage!=null) stb.append(": ").append(errorMessage);
		return stb.toString();
	}

	@Nullable public static BaitType tryParse(@NotNull String string){
		return tryParse(string, null);
	}
	@Nullable public static BaitType tryParse(@NotNull String string, @Nullable Consumer<String> errorMessageConsumer){
		lock.readLock().lock();
		BaitType baitType = cache.get(string);
		lock.readLock().unlock();
		if(baitType==null){
			lock.writeLock().lock();
			baitType = parseNew(string, errorMessageConsumer);
			if(baitType!=null){
				cache.put(string, baitType);
				lock.writeLock().unlock();
			}
		}
		return baitType;
	}

	@Nullable private static BaitType parseNew(@NotNull String string, @Nullable Consumer<String> errorMessageConsumer){
		int separation = string.lastIndexOf('/');

		String childText = separation>=0 ? string.substring(separation+1) : string;
		if(!validateTypeString(childText)){
			if(errorMessageConsumer!=null) errorMessageConsumer.accept("Invalid bait type '"+childText+"'");
			return null;
		}

		@Nullable BaitType parent;
		if(separation>=0){
			String parentText = string.substring(0, separation);
			parent = cache.get(parentText);
			if(parent==null){
				parent = parseNew(parentText, errorMessageConsumer);
				if(parent==null) return null;
				else cache.put(parentText, parent);
			}
		}else parent = null;

		return new BaitType(childText, parent);
	}

	private static boolean validateTypeString(@NotNull String text){
		PrimitiveIterator.OfInt it = text.chars().iterator();
		while(it.hasNext()){
			int ch = it.nextInt();
			if(!isValidChar(ch)) return false;
		}
		return true;
	}

	private static boolean isValidChar(int c){
		return c>='0'&&c<='9'||c>='a'&&c<='z'||c=='_'||c==':'||c=='.'||c=='-';
	}

	private final @NotNull String text;
	private final @Nullable BaitType parent;

	private BaitType(@NotNull String text, @Nullable BaitType parent){
		Objects.requireNonNull(text, "type == null");
		if(text.isEmpty()) throw new IllegalArgumentException("Empty string cannot be used as bait type");
		if(!NAME_REGEX.matcher(text).matches())
			throw new IllegalArgumentException("Invalid bait type '"+text+"'");
		this.text = text;
		this.parent = parent;
	}

	@NotNull public String text(){
		return this.text;
	}
	@Nullable public BaitType parent(){
		return this.parent;
	}

	private int depth = -1;

	public int depth(){
		if(this.depth==-1) return this.depth = this.parent==null ? 0 : this.parent.depth()+1;
		return this.depth;
	}

	private String translateKey;

	@NotNull public String getTranslationKey(){
		if(this.translateKey==null){
			StringBuilder stb = new StringBuilder();
			buildTranslateKeyRecursive(stb);
			this.translateKey = stb.toString();
		}
		return this.translateKey;
	}

	private void buildTranslateKeyRecursive(StringBuilder stb){
		if(this.parent!=null) this.parent.buildTranslateKeyRecursive(stb);
		else stb.append("bait_type.tfts");
		stb.append(".").append(this.text);
	}

	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(obj==null) return false;
		return obj instanceof BaitType type&&
				Objects.equals(this.text, type.text)&&
				Objects.equals(this.parent, type.parent);
	}

	@Override public int hashCode(){
		return Objects.hash(text, parent);
	}

	@Override public String toString(){
		return this.parent==null ? this.text : this.parent+"/"+this.text;
	}
}
