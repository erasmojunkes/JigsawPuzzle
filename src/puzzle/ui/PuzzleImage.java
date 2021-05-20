/*
 * PuzzleImage.java
 *
 * Created on 29. August 2006, 18:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import puzzle.GameCommander;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;

/**
 * stores information about a picture of the puzzle, holds the (if needed)
 * resized picture
 * 
 * first call the isResizableToGoodQuality with the side length of your wish
 * after that one call call resize() to do the resizing.
 * 
 * @author Heinz
 */
/**
 * armazena informa��es sobre uma imagem do quebra-cabe�a, mant�m o (se necess�rio)
 * imagem redimensionada
 * 
 * primeiro chame o isResizableToGoodQuality com o comprimento lateral do seu desejo
 * depois disso chame resize () para fazer o redimensionamento.
 * 
 * @autor Heinz
 */
public class PuzzleImage implements Storeable {

	/**
	 * decisiion of good/bad qualtity, if one side is more than this factor
	 * rescaled than it might be a bad quality TODO find out if there is a
	 * visual difference between up and downscale and possibly we need two
	 * values one for up and one for down scaling
	 */
	/**
	 * decis�o de boa / m� qualidade, se um lado for mais do que este fator
	 * reescalonado do que pode ser uma m� qualidade TODO descobrir se h� um
	 * diferen�a visual entre up e downscale e possivelmente precisamos de dois
	 * valores um para aumento e um para redu��o
	 */
	private static final double QUALTITY_RESIZE_OFFSET = 0.22d;

	public static BufferedImage getImageFromFile(File imageFile) throws JigsawPuzzleException {
		BufferedImage theImage = null;
		try {
			theImage = ImageIO.read(imageFile);
		} catch (IOException ex) {
			throw new JigsawPuzzleException("file is not a valid image format.", ex);
		}

		return theImage;
	}
	
	public static BufferedImage getImageFromURL(URL imageURL) throws JigsawPuzzleException {
		BufferedImage theImage = null;
		try {
			theImage = ImageIO.read(imageURL);
		} catch (IOException ex) {
			throw new JigsawPuzzleException("URL is not a valid one.", ex);
		}

		return theImage;
	}
	
	public static BufferedImage getImageFromStream(InputStream imageStream) throws JigsawPuzzleException {
		BufferedImage theImage = null;
		try {
			theImage = ImageIO.read(imageStream);
		} catch (IOException ex) {
			throw new JigsawPuzzleException("file is not a valid image format.", ex);
		}

		return theImage;
	}

	/**
	 * the original image (original in size)
	 */
	/**
	 * a imagem original (tamanho original)
	 */
	private BufferedImage originalImage; // the pic in the new sized form

	/**
	 * the rescaled image to fit to the sideLength of the puzzle game
	 */
	/**
	 * a imagem redimensionada para caber no comprimento lateral do jogo de quebra-cabe�a
	 */
	private BufferedImage resizedImage;

	/**
	 * new size of the image
	 */
	/**
	 * novo tamanho da imagem
	 */
	private Dimension resampleSize;

	/**
	 * a constructor for storage
	 */
	/**
	 * um construtor para armazenamento
	 */
	public PuzzleImage() {

	}

	public PuzzleImage(BufferedImage image) {
		this.originalImage = image;
	}

	public PuzzleImage(File imageFile) throws JigsawPuzzleException {
		this.originalImage = getImageFromFile(imageFile);
	}
	
	public PuzzleImage(URL imageURL) throws JigsawPuzzleException {
		this.originalImage = getImageFromURL(imageURL);
	}
	
	public PuzzleImage(InputStream imageStream) throws JigsawPuzzleException {
		this.originalImage = getImageFromStream(imageStream);
	}

	public int getHeight() {
		return this.resizedImage.getHeight();
	}

	public int getWidth() {
		return this.resizedImage.getWidth();
	}

	public BufferedImage getOriginalImage() {
		return this.originalImage;
	}

	/**
	 * retrieves the whole image (for preview etc.)
	 */
	/**
	 * recupera a imagem inteira (para visualiza��o etc.)
	 */
	public BufferedImage getImage() {
		return this.resizedImage;
	}

	/**
	 * retrieves a specific part of the picture for use in the single pieces
	 * 
	 * @param column
	 *            a specific column index the image should be from (0 to
	 *            count-1)
	 * @param row
	 *            the row index the image should be from (0 to count-1)
	 * @return the image for column and row
	 */
	/**
	 * recupera uma parte espec�fica da imagem para uso nas pe�as individuais
	 * 
	 * @param coluna
	 * 			  um �ndice de coluna espec�fico em que a imagem deve ser (0 a
	 * 			  contagem-1)
	 * @param linha
	 *            o �ndice de linha da imagem deve ser (0 a contagem-1)
	 * @return a imagem para coluna e linha
	 */
	public BufferedImage getImage(int column, int row) {
		int sideLength = GameCommander.getInstance().getPreferences()
				.getSideLength();
		final int x = column * sideLength; // x coord
		final int y = row * sideLength; // y coord

		final int halfSideLength = (sideLength / 2);

		// here are the later coordinates for the image that should be retrieved
		// from the original bigger image, so x and y are the upper left point
		// and together with width they are entirely in the original image
		// aqui est�o as �ltimas coordenadas para a imagem que deve ser recuperada
		// da imagem original maior, ent�o x e y s�o o ponto superior esquerdo
		// e junto com a largura, eles est�o inteiramente na imagem original
		final int sectionX, sectionY, sectionWidth, sectionHeight;

		boolean firstColumn = false;
		boolean lastColumn = false;
		boolean firstRow = false;
		boolean lastRow = false;

		// be sure to cut out a image part that is big enough, therefor try to
		// cut out an image that has twice the area of the really needed image.
		// so the x point lies half side length more left and more high than the
		// piece starts. Only at the border you have to choose smaller.
		// certifique-se de cortar uma parte da imagem que seja grande o suficiente, portanto, tente
		// recorte uma imagem que tenha o dobro da �rea da imagem realmente necess�ria.
		// ent�o o ponto x fica na metade do comprimento do lado mais � esquerda e mais alto do que o
		// a pe�a come�a. S� na borda voc� tem que escolher o menor.
		if (column == 0) { // first column // primeira coluna
			sectionX = 0; // if column == null -> x == 0
			sectionWidth = sideLength * 2;
			firstColumn = true;
		} else if (column == GameCommander.getInstance().getPreferences()
				.getColumns() - 1) { // last column // �ltima coluna
			sectionX = x - halfSideLength;
			sectionWidth = sideLength * 2 - halfSideLength;
			lastColumn = true;
		} else { // some column between // alguma coluna entre
			sectionX = x - halfSideLength;
			sectionWidth = sideLength * 2;
		}

		if (row == 0) { // first row // primeira linha
			sectionY = 0; // if row == 0 -> y == 0
			sectionHeight = sideLength * 2;
			firstRow = true;
		} else if (row == GameCommander.getInstance().getPreferences()
				.getRows() - 1) { // last row // �ltima linha
			sectionY = y - halfSideLength;
			sectionHeight = sideLength * 2 - halfSideLength;
			lastRow = true;
		} else { // some row between // alguma linha entre
			sectionY = y - halfSideLength;
			sectionHeight = sideLength * 2;
		}

		BufferedImage returnImage = new BufferedImage(sideLength * 2,
				sideLength * 2, resizedImage.getType());
		// BufferedImage.TYPE_USHORT_555_RGB);

		BufferedImage sectionImage = null;
		try {
			sectionImage = resizedImage.getSubimage(sectionX, sectionY,
					sectionWidth, sectionHeight);
		} catch (RasterFormatException RFE) {
			System.out.println(RFE.getMessage());
			throw new RuntimeException("no subimage could be created - error");
		}
		Graphics2D returnImageGraphics = (Graphics2D) returnImage.getGraphics();

		int subImageStartX, subImageStartY;

		if (firstColumn) {
			subImageStartX = sideLength / 2;
		} else
			subImageStartX = 0;
		if (firstRow) {
			subImageStartY = sideLength / 2;
		} else
			subImageStartY = 0;

		returnImageGraphics.drawImage(sectionImage, subImageStartX,
				subImageStartY, sectionWidth, sectionHeight, null);
		returnImageGraphics.dispose();

		return returnImage;
	}

	/**
	 * tests if this PuzzleImage is resiable within certain parameters
	 * @param sideLength
	 * @return true if the size doesn't suffer too much
	 */
	/**
	 * testa se este PuzzleImage � resiable dentro de certos par�metros
	 * @param comprimento lateral
	 * @return verdadeiro se o tamanho n�o sofrer muito
	 */
	public boolean isResizableToGoodQuality(int sideLength) {
		final int imgWidth = this.originalImage.getWidth();
		final int imgHeight = this.originalImage.getHeight();

		int widthAbove = imgWidth % sideLength;
		int heightAbove = imgHeight % sideLength;

		int widthBelow = sideLength - widthAbove;
		int heightBelow = sideLength - heightAbove;

		int newWidth, newHeight;
		double widthChangePercentage, heightChangePercentage;

		if (widthBelow < widthAbove) { // it's better to scale up // � melhor aumentar
			newWidth = imgWidth + widthBelow;
			// new width is bigger than old so the result is 1.xx to get
			// percentage substract 1
			// a nova largura � maior do que a antiga, ent�o o resultado � 1.xx para obter
			// porcentagem de substrato 1
			widthChangePercentage = (newWidth / (double) imgWidth) - 1;
		} else { // it's better to scale down // � melhor reduzir
			newWidth = imgWidth - widthAbove;
			// new one is smaller
			// o novo � menor
			widthChangePercentage = (imgWidth / (double) newWidth) - 1;
		}

		if (heightBelow < heightAbove) { // better scale up // melhor escalar
			newHeight = imgHeight + heightBelow;
			heightChangePercentage = (newHeight / (double) imgHeight) - 1;
		} else { // better scale down // melhor reduzir
			newHeight = imgHeight - heightAbove;
			heightChangePercentage = (imgHeight / (double) newHeight) - 1;
		}

		// calculate if the resize factor is within the range from 0 to
		// calcule se o fator de redimensionamento est� dentro do intervalo de 0 a
		// QUALTITY_RESIZE_OFFSET
		this.resampleSize = new Dimension(newWidth, newHeight);
		if ((widthChangePercentage < QUALTITY_RESIZE_OFFSET)
				&& (heightChangePercentage < QUALTITY_RESIZE_OFFSET)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * resizes this image to the size that isResizableToGoodQuality sets
	 * call this method to actively resize this instance.
	 */
	/**
	 * redimensiona esta imagem para o tamanho que isResizableToGoodQuality define
	 * chame este m�todo para redimensionar ativamente esta inst�ncia.
	 */
	public void resize() {
		if (resampleSize != null) {
			Image rescaled = originalImage.getScaledInstance(
					this.resampleSize.width, this.resampleSize.height,
					Image.SCALE_SMOOTH);
			if (rescaled instanceof BufferedImage) {
				this.resizedImage = (BufferedImage) rescaled;
			} else {
				BufferedImage newone = new BufferedImage(
						this.resampleSize.width, this.resampleSize.height,
						BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g_newone = newone.createGraphics();
				g_newone.drawImage(rescaled, 0, 0, null);
				g_newone.dispose();
				this.resizedImage = newone;
			}
		} else {
			throw new RuntimeException(
					"call the isResizable method first - this will init");
		}
	}

	/**
	 * resizes this image so, that his aspect is hold but resized to fit
	 */
	/**
	 * redimensiona esta imagem para que seu aspecto seja mantido, mas redimensionado para caber
	 */
	public Image resizeToFit(Dimension d) {
		int heightImage = this.originalImage.getHeight();
		int widthImage = this.originalImage.getWidth();

		double widthRatio = d.width / (double) widthImage;
		double heightRatio = d.height / (double) heightImage;

		// test which from those above are nearer to 0
		// teste quais daqueles acima est�o mais pr�ximos de 0
		double widthRatioAbs = Math.abs(widthRatio);
		double heightRatioAbs = Math.abs(heightRatio);

		// new x and y
		// novo x e y
		double x, y;

		if (widthRatioAbs < heightRatioAbs) {
			x = widthImage * widthRatio;
			y = heightImage * widthRatio;
		} else {
			x = widthImage * heightRatio;
			y = heightImage * heightRatio;
		}
		return this.originalImage.getScaledInstance((int) x, (int) y,
				BufferedImage.SCALE_SMOOTH);
	}

	public Dimension getResampleSize() {
		return resampleSize;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		System.out.println("write image");
		ImageIO.write(this.originalImage, "png", out);
		out.flush();
		System.out.println("wrote image");

		System.out.println("write dimension");
		out.writeObject(this.resampleSize);
		System.out.println("wrote dimension");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		System.out.println("read image");
		this.originalImage = ImageIO.read(in);
		System.out.println("read image");

		System.out.println("read dim");
		Dimension d = (Dimension) in.readObject();
		System.out.println("read dim");

		this.resampleSize = d;
		this.resize();
	}

	@Override
	public void restore(Node current) throws LoadGameException {

		Node puzzleImage = StorageUtil.findDirectChildNode(current, "PuzzleImage");

		byte[] imageData = StorageUtil.restoreBinaryData(puzzleImage, "ImageData");
		ByteArrayInputStream imageByteStream = new ByteArrayInputStream(
				imageData);
		try {
			this.originalImage = ImageIO.read(imageByteStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadGameException(e);
		}

		// get the primitives
		// obter os primitivos
		NamedNodeMap nnm = puzzleImage.getAttributes();
		Node item;

		item = nnm.getNamedItem("resampleSizeWidth");
		int width = Integer.parseInt(item.getNodeValue());

		item = nnm.getNamedItem("resampleSizeHeigth");
		int heigth = Integer.parseInt(item.getNodeValue());

		this.resampleSize = new Dimension(width, heigth);
		this.resize(); // inits the resizedImage // inicia o resizedImage
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Element image = doc.createElement("PuzzleImage");

		ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
		// write the image in the format to the byte stream array
		// escreve a imagem no formato para a matriz de fluxo de bytes
		try {
			ImageIO.write(this.originalImage, "png", imageByteStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SaveGameException(e);
		}
		StorageUtil.storeBinaryData(image, "ImageData", imageByteStream
				.toByteArray());

		// write the primitives 
		// escreva os primitivos
		image.setAttribute("resampleSizeWidth", "" + this.resampleSize.width);
		image.setAttribute("resampleSizeHeigth", "" + this.resampleSize.height);
		// store to the current element
		// armazena no elemento atual
		current.appendChild(image);
	}

}
