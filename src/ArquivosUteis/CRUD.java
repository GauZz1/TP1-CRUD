package ArquivosUteis;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class CRUD<T extends Registro> {

  private RandomAccessFile arquivo;
  private HashExtensivel<ParIDEndereco> indiceDireto;
  private Constructor<T> construtor;
  private int TAMANHO_CABECALHO = 4;

  public CRUD(String nomeArquivo, Constructor<T> c) throws Exception {
    arquivo = new RandomAccessFile("dados/" + nomeArquivo, "rw");
    indiceDireto = new HashExtensivel<>(
        ParIDEndereco.class.getConstructor(),
        4,
        "dados/" + "indiceID.1.db",
        "dados/" + "indiceID.2.db");
    construtor = c;
    if (arquivo.length() < TAMANHO_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }

  public int create(T entidade) throws Exception {
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    int novoID = ultimoID + 1;
    entidade.setID(novoID);
    arquivo.seek(0);
    arquivo.writeInt(novoID);

    // Movimenta o ponteiro do arquivo para o ponto de inserção do novo registro
    arquivo.seek(arquivo.length());
    long endereco = arquivo.getFilePointer();

    // Cria o registro no arquivo
    byte[] ba = entidade.toByteArray();
    arquivo.writeByte('#'); // # -> registro válido; * -> registro excluído
    arquivo.writeShort(ba.length);
    arquivo.write(ba);
    indiceDireto.create(new ParIDEndereco(novoID, endereco));
    return novoID;
  };

  public T read(int id) throws Exception {

    ParIDEndereco p = indiceDireto.read(id);
    if (p == null)
      return null;

    arquivo.seek(p.getEndereco());

    byte lapide = arquivo.readByte();
    int tamanho = arquivo.readShort();
    byte[] ba = new byte[tamanho];
    if (lapide == '#') {
      arquivo.read(ba);
      T entidade = construtor.newInstance();
      entidade.fromByteArray(ba);
      if (entidade.getID() == id)
        return entidade;
    }
    return null;
  };

  public boolean update(T novaEntidade) throws Exception {
	  
	ParIDEndereco p = indiceDireto.read(novaEntidade.getID());
	if (p == null)
	      return false;
	
	arquivo.seek(p.getEndereco());
	
	arquivo.readByte();
    int tamanho = arquivo.readShort();
    
    if(novaEntidade.toByteArray().length <= tamanho) {
    	arquivo.write(novaEntidade.toByteArray());
    	return true;
    }
    else {
    	arquivo.seek(p.getEndereco());
    	arquivo.writeByte('*');
    	arquivo.seek(arquivo.length());
    	long endereco = arquivo.getFilePointer();
    	
    	byte[] ba = novaEntidade.toByteArray();
    	arquivo.writeByte('#');
    	arquivo.writeShort(ba.length);
    	arquivo.write(ba);
    	indiceDireto.update(new ParIDEndereco(novaEntidade.getID(), endereco));

    	return true;
    }
  
  };

  public boolean delete(int id) throws Exception {
	  ParIDEndereco p = indiceDireto.read(id);
	    if (p == null)
	      return false;
	    arquivo.seek(p.getEndereco());
	    arquivo.writeByte('*');
	    return true;
	    
  };

}
