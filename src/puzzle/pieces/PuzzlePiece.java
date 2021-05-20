/*
 * PuzzleStueck.java
 *
 * Created on 27. August 2006, 15:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.pieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Vector;

import puzzle.GameCommander;
import puzzle.GamePreferences;
import puzzle.Offset;
import puzzle.PuzzleProperties;
import puzzle.Turnable;
import puzzle.edge.Edge;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.Storeable;

/**
 * 
 * @author Heinz
 */
/**
 * 
 * @autor Heinz
 */

public abstract class PuzzlePiece implements Storeable, Turnable {

	/**
	 * Additional Gap for painting issues. If you do not provide 1-2 points at
	 * least, you cannot use the outline because it might be drawn outside the
	 * shape and then you get graphical bugs
	 */
	/**
	 * Lacuna adicional para problemas de pintura. Se voc� n�o fornecer 1-2 pontos em
	 * pelo menos, voc� n�o pode usar o contorno porque pode ser desenhado fora do
	 * forma e, em seguida, voc� obt�m erros gr�ficos
	 */
	protected static final int GAP_X = 5;

	/**
	 * gap y
	 * VIEW
	 */
	/**
	 * gap y
	 * VISUALIZAR
	 */
	protected static final int GAP_Y = 5;

	/**
	 * gap widht
	 * VIEW
	 */
	/**
	 * largura da lacuna
	 * VISUALIZAR
	 */
	protected static final int GAP_WIDTH = 10;

	/**
	 * gap height
	 * VIEW
	 */
	/**
	 * altura da lacuna
	 * VISUALIZAR
	 */
	protected static final int GAP_HEIGHT = 10;

	/**
	 * the shape (the outline shape of that puzzle piece)
	 * VIEW
	 */
	/**
	 * a forma (o contorno da pe�a do quebra-cabe�a)
	 * VISUALIZAR
	 */
	protected transient Shape puzzleShape;
	
	/**
	 * inidcates if this puzzle piece is highlighted or not
	 * VIEW
	 */
	/**
	 * indica se esta pe�a do quebra-cabe�a est� destacada ou n�o
	 * VISUALIZAR
	 */
	protected boolean highlighted = false;
	
	/**
	 * todas as arestas que esta pe�a possui.
	 * MODELO
	 */
	protected transient List<Edge> edges;
	
	/**
	 * if piece is within this point true, false otherwise
	 * VIEW
	 */
	/**
	 * se a pe�a est� dentro deste ponto verdadeiro, falso caso contr�rio
	 * VISUALIZAR
	 */
	public abstract boolean isHit(Point punkt);

	/**
	 * definir destaque
	 * VISUALIZAR
	 */
	public void highlight() {
		this.highlighted = true;
	}
	
	/**
	 * reset the higlight.
	 * VIEW
	 */
	/**
	 * redefinir o destaque.
	 * VISUALIZAR
	 */
	public void unhighlight() {
		this.highlighted = false;
	}
	
	/**
	 * paints this piece in the given rectangle clipping area
	 * VIEW
	 */
	/**
	 * pinta esta pe�a na �rea de recorte do ret�ngulo fornecida
	 * VISUALIZAR
	 */
	public final void renderInClip(Graphics2D g2d) throws JigsawPuzzleException {
		Rectangle rect = this.getBoundingRectangle();
		GamePreferences gp = GameCommander.getInstance().getPreferences();
		// shadow is the first
		// sombra � a primeira
		if (gp.isShowShadow()) {
			renderShadowInClip(g2d, rect);
		}
		// than draw the face
		// ent�o desenhe o rosto
		renderFaceInClip(g2d, rect);
		
		// finally draw the outline
		// finalmente desenhe o contorno
		if (gp.isShowOutline() || (this.highlighted && gp.isHighlight())) {
			renderOutlineInClip(g2d, rect);
		}
		
	}

	/**
	 * draw the face of the puzzle piece
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	/**
	 * desenhe o rosto da pe�a do quebra-cabe�a
	 * VISUALIZAR
	 * @param g2d
	 * @param rect
	 */
	protected abstract void renderFaceInClip(Graphics2D g2d, Rectangle rect);

	/**
	 * draw the outlined Shape of the piece
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	/**
	 * desenhe a forma delineada da pe�a
	 * VISUALIZAR
	 * @param g2d
	 * @param rect
	 */
	protected void renderShadowInClip(Graphics2D g2d, Rectangle rect) {
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);

		int shadowLength = GameCommander.getInstance().getPreferences()
				.getShadowLength();
		AffineTransform mover = AffineTransform.getTranslateInstance(
				shadowLength, shadowLength);
		Shape shadowShape = mover.createTransformedShape(this.puzzleShape);
		g2d.setColor(PuzzleProperties.PIECE_SHADOW_COLOR);
		g2d.fill(shadowShape);
	}
	
	/**
	 * draws the outline of this shape within the clips
	 * VIEW
	 * @param g2d
	 * @param rect
	 */
	/**
	 * desenha o contorno desta forma dentro dos clipes
	 * VISUALIZAR
	 * @param g2d
	 * @param rect
	 */
	protected void renderOutlineInClip(Graphics2D g2d, Rectangle rect) {
		g2d.setClip(rect.x, rect.y, rect.width, rect.height);
		GamePreferences gp = GameCommander.getInstance().getPreferences();

		g2d.setStroke(gp.getOutlineStroke());
		
		// set highlight or not.
		// definir destaque ou n�o.
		if (!this.highlighted || !gp.isHighlight())
			g2d.setColor(PuzzleProperties.PIECE_COLOR);
		else
			g2d.setColor(PuzzleProperties.PIECE_HIGHLIGHTED_COLOR);
		
		g2d.draw(this.puzzleShape);
	}

	/**
	 * returns a bounding box (mustn't be the smallest)
	 * VIEW
	 */
	/**
	 * retorna uma caixa delimitadora (n�o deve ser a menor)
	 * VISUALIZAR
	 */
	public abstract Rectangle getBoundingRectangle() throws JigsawPuzzleException ;

	/**
	 * Test if this piece is within the given rectangle (either entirely or
	 * partially)
	 * VIEW
	 * @return false if there is no area that belongs to both (the specified
	 *         rect and the one from this piece)
	 */
	/**
	* Teste se esta pe�a est� dentro do ret�ngulo dado (totalmente ou
	* parcialmente)
	* VISUALIZAR
	* @return false se n�o houver nenhuma �rea que perten�a a ambos (o especificado
	* rect e o desta pe�a)
	*/
	public abstract boolean isWithinRectangle(Rectangle rect);
	
	/**
	 * returns the shape
	 * VIEW
	 */
	/**
	 * retorna a forma
	 * VISUALIZAR
	 */
	public Shape getShape() throws JigsawPuzzleException {
		return this.puzzleShape;
	}
	
	/**
	 * a method to rebuild the shape which one can get with getShape() afterwards.
	 */
	/**
	 * um m�todo para reconstruir a forma que se pode obter com getShape () posteriormente.
	 */
	protected abstract void buildShape() throws JigsawPuzzleException ;

	/**
	 * test if this piece is near (MAX_SNAP_DISTANCE) to another "brother"
	 * piece, if so it will snap to this one. Meaning that it will additionally
	 * move the remainig length to the other piece and returns true if something
	 * to snap to was found and this was snapped to, false otherwise
	 * @throws JigsawPuzzleException 
	 */
	/**
	 * teste se esta pe�a est� perto (MAX_SNAP_DISTANCE) de outro "irm�o"
	 * pe�a, em caso afirmativo, ele se ajustar� a esta. O que significa que ser� adicionalmente
	 * move o comprimento restante para a outra pe�a e retorna verdadeiro se algo
	 * para ajustar foi encontrado e este foi ajustado, caso contr�rio, falso
	 * @throws JigsawPuzzleException 
	 */

	public PuzzlePiece snap() throws JigsawPuzzleException {
		
		GameCommander gC = GameCommander.getInstance();

		List<Edge> myResolvableEdges = this.getResolvableEdges();
		List<PuzzlePiece> allPieces = gC.getPieceDisposer().getPuzzlePieces();
		for (Edge ownEdge : myResolvableEdges) { // for all own open edges
												 // para todas as pr�prias bordas abertas

			// edgeNumber -> get contrary type
			// edgeNumber -> obter tipo contr�rio
			int ownEdgeNumber = ownEdge.getEdgePairNumber();
			Edge.Type contraryEdgeType = Edge.contraryEdgeChar(ownEdge.getType());

			for (PuzzlePiece piece : allPieces) { // for every piece
												  // para cada pe�a
				if (piece == this) {
					continue; // if same contine
							  // se o mesmo continuar
				}
				// get all edges of the desired type (contraryEdgeType)
				// obt�m todas as bordas do tipo desejado (counterEdgeType)
				List<Edge> list = piece.getResolvableEdges(contraryEdgeType);
				if (list.isEmpty()) {
					continue; // if none continue
							  // se nenhum continuar
				}
				// find the contrary edge by it's number
				// encontre a borda contr�ria pelo seu n�mero
				Edge contraryEdge = null;
				for (Edge k : list) {
					if (k.getEdgePairNumber() == ownEdgeNumber) {
						contraryEdge = k;
						break;
					}
				}

				if (contraryEdge == null) {
					continue; // if the contrary Edge not in this piece go on
							  // se o contr�rio Edge n�o estiver nesta pe�a, continue
				}

				Point firstPoint = ownEdge.calculatePoint();
				Point secondPoint = contraryEdge.calculatePoint();

				final double dist = firstPoint.distance(secondPoint);

				if (dist < PuzzleProperties.MAX_SNAP_DISTANCE) {

					Offset offToFit = new Offset(secondPoint.x - firstPoint.x,
							secondPoint.y - firstPoint.y);

					this.move(offToFit);
					// retrieve the new piece
					// recupere a nova pe�a
					PuzzlePiece pp = GameCommander.getInstance().getPieceDisposer().assamblyPieces(this, ownEdge,
									piece, contraryEdge);
					return pp;
				}
			}
		}
		return null;
	}

	/**
	 * to retrieve all edges
	 * MODEL
	 */
	/**
	 * para recuperar todas as bordas
	 * MODELO
	 */
	public List<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * to retrieve all edges from the parameter type
	 * MODEL
	 */
	/**
	 * para recuperar todas as arestas do tipo de par�metro
	 * MODELO
	 */
	public abstract List<Edge> getEdges(Edge.Type typ);

	/**
	 * to retrieve a specific edge with the parameter number
	 * MODEL
	 */
	/**
	* para recuperar uma borda espec�fica com o n�mero do par�metro
	* MODELO
	*/
	public Edge getEdge(int edgeNumber) {
		List<Edge> liste = this.getEdges();

		for (Edge k : liste) {
			if (k.getEdgePairNumber() == edgeNumber)
				return k;
		}
		return null;
	}

	/**
	 * retrieve edges that are not already closed!
	 * Also edges at the border of the puzzle
	 * are returned, because they will never be closed!
	 * MODEL
	 */
	/**
	 * recupere bordas que ainda n�o est�o fechadas!
	 * Tamb�m bordas na borda do quebra-cabe�a
	 * s�o devolvidos, pois nunca ser�o fechados!
	 * MODELO
	 */
	public List<Edge> getOpenEdges() {
		List<Edge> liste = this.getEdges();
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if (k.isOpen())
				erg.add(k);
		}

		return erg;
	}
	
	/**
	 * retrieve an open edge with the specified number, or null if such an edge
	 * wasn't found
	 * MODEL
	 */
	/**
	 * recuperar uma borda aberta com o n�mero especificado, ou nulo se tal borda* n�o foi encontrado
	 * MODELO
	 */
	public Edge getOpenEdge(int edgeNumber) {
		List<Edge> liste = this.getOpenEdges();

		for (Edge k : liste) {
			if (k.isOpen() && k.getEdgePairNumber() == edgeNumber)
				return k;
		}
		return null;

	}
	
	/**
	 * retrieve edges that have a counterpart in this game, and 
	 * are not already connected to that counterpart.
	 * MODEL
	 * @return
	 */
	/**
	 * recuperar arestas que t�m uma contrapartida neste jogo, e
	 * ainda n�o est�o conectados a essa contraparte.
	 * MODELO
	 * @return
	 */
	public List<Edge> getResolvableEdges() {
		List<Edge> liste = this.getEdges();
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if (k.isResolvable())
				erg.add(k);
		}

		return erg;
	}

	/**
	 * retrieves the open resolvable(open and not NULL type) of the specified type
	 * MODEL
	 */
	/**
	 * recupera o aberto resolv�vel (tipo aberto e n�o NULL) do tipo especificado
	 * MODELO
	 */
	public List<Edge> getResolvableEdges(Edge.Type edgeType) {
		List<Edge> liste = this.getEdges(edgeType);
		List<Edge> erg = new Vector<Edge>();

		for (Edge k : liste) {
			if ((k.isOpen()))
				erg.add(k);
		}
		return erg;
	}

	

	/**
	 * Retrieves 1 for single pieces, and the number of single pieces for multi
	 * pieces
	 * MODEL
	 */
	/**
	 * Recupera 1 para pe�as individuais e o n�mero de pe�as individuais para multi
	 * pe�as
	 * MODELO
	 */
	public abstract int getPieceCount();

	/**
	 * moves the piece the specified offset
	 * MODEL
	 */
	/**
	 * move a pe�a no deslocamento especificado
	 * MODELO
	 */
	public abstract void move(Offset to);

}
