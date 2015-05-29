package dove.cmd;

import dove.cmd.model.datatypes.Bool;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.datatypes.Integral;
import dove.cmd.model.datatypes.Text;

import java.io.*;
import java.nio.ByteBuffer;

public class ModelLoader {
    private static final File file = new File("../resources/cmd/model.bin");

    public void load(CommandLineData data)
            throws LoaderException {
        try (FileInputStream fis = new FileInputStream(file)) {

        } catch (IOException e) {
            throw new LoaderException("Failed to load data - cause: " + e.getMessage());
        }
    }

    public void save(CommandLineData data) {

    }

    private void write(Data data, OutputStream os)
            throws IOException {
        os.write(data.getType().ordinal());

        switch (data.getType()) {
            case FLOAT: {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.putDouble(((dove.cmd.model.datatypes.Float) data).getVal());
                os.write(buffer.array());

                buffer.clear();
            }
            break;

            case INTEGRAL: {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.putLong(((Integral) data).getVal());
                os.write(buffer.array());

                buffer.clear();
            }
            break;

            case BOOL:
                os.write((((Bool) data).getVal() ? 1 : 0));
                break;

            case TEXT: {
                Text text = (Text) data;

                os.write(text.getVal().length() >> 8);
                os.write(text.getVal().length());
                os.write(text.getVal().getBytes());
            }
            break;

            case METHOD:
                break;

            case INSTANCE:
                break;

            case STRUCTURE:
                break;

            case FIELD:
                break;
        }
    }

    private Data read(InputStream is) {
        return null;
    }

    public static class LoaderException
            extends Exception {
        public LoaderException(String msg) {
            super(msg);
        }
    }
}
