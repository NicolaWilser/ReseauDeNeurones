package ReseauDeNeurones;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Classe creant et gerant le reseau de neurones. 
 * @author e1502316 Nicola Wilser
 *
 */

public class reseau{
	/**
	 * Couche d'entree. 
	 */
	public couche coucheEntree;
	/**
	 * Couche cachee ou intermediaire. 
	 */
	public couche coucheCachee;
	/**
	 * Couche de sortie. 
	 */
	public couche coucheSortie; 
	/**
	 * Tableau contenant la valeur des sorties theoriques souhaitees dans le cadre d'un apprentissage. 
	 */
	public ArrayList <Double> sortieTheorique; 
	/**
	 * Tableau contenant la valeur des sorties reelles actuelles. 
	 */
	public ArrayList <Double> sortieActuelle; 
	/**
	 * Erreur toleree entre la sortie theorique et la sortie actuelle pour etre consideree comme valide. 
	 */
	public double erreurToleree = 0.05;
	/**
	 * Hauteur de l'image. 
	 */
	public int hauteur;
	/**
	 * Largeur de l'image. 
	 */
	public int largeur;
	/**
	 * Constructeur par defaut qui initialise les couches a des couches vides. 
	 */
	public reseau()
	{
		coucheEntree = new couche();
		coucheCachee = new couche();
		coucheSortie = new couche(); 
		sortieActuelle = new ArrayList <Double>();
		sortieTheorique = new ArrayList <Double>();
		hauteur = 0;
		largeur = 0;
	}
	/**
	 * Initialise le biais de la couche placee en parametre. 
	 * @param c (Couche) Couche pour la quelle on souhaite initialiser le biais. 
	 * @param valeurBiais (Double) La valeur du biais souhaite.
	 * @param tailleCoucheActuelle (Double) Le nombre de neurones dans la couche actuelle.
	 */
	public void initialiserBiais(couche c, double valeurBiais, int tailleCoucheActuelle)
	{
		c.biais.setValeur(valeurBiais);
		c.biais.genererPoids(tailleCoucheActuelle);
	}
	/**
	 * Initialise la couche placee en parametre en generant les neurones. 
	 * @param c (Couche) Couche pour la quelle on souhaite l'initialiser. 
	 * @param valeurBiais (Double) La valeur du biais souhaite.
	 * @param tailleCoucheSuivante (Double) Le nombre de neurones dans la couche suivante. 
	 */
	public void initialiserCouche(couche c, double valeurBiais, int tailleCoucheSuivante)
	{
		neurone n = new neurone();
		n.genererPoids(tailleCoucheSuivante);
		c.neurones.add(n);
	}
	/**
	 * Initialise les sorties. 
	 * @param taille (Entier) Nombre de sorties à initialiser. 
	 */
	/*
	 * Cree suffisament d'elements dans les sorties et initialise 
	 * differement les sorties theoriques et actuelles
	 * pour eviter d'avoir des faux positifs a l'initialisation. 
	 */
	public void initialiserSorties(int taille)
	{
		int i = 0;
		while (sortieTheorique.size() < taille)
		{
			sortieTheorique.add((double) i);
			sortieActuelle.add((double) i+2);
			i++;
		}
	}
	/**
	 * Affiche la couche sortie avec la valeur des differents neurones. 
	 */
	void afficherCoucheSortie()
	{
		String tmp = "";
		for (int i = 0; i < coucheSortie.neurones.size(); i++)
		{
			tmp += "Sortie actuelle "+Integer.toString(i)+" = "+Double.toString(sortieActuelle.get(i))+"\n";
		}
		IJ.showMessage(tmp);
	}
	/**
	 * Initialise le reseau de neurones en creant les neurones des differentes couches.
	 * @param hauteur (Entier) Hauteur de l'image que l'on analyse.
	 * @param largeur (Entier) Largeur de l'image que l'on analyse.
	 */
	public void initialiser(int hauteur, int largeur)
	{
		this.hauteur = hauteur;
		this.largeur = largeur;
		int tailleCoucheEntree = hauteur*largeur;
		int tailleCoucheCachee = 75;
		int tailleCoucheSortie = 10;
		double valeurBiais = 1.0;
		for (int i = 0; i < tailleCoucheEntree; i++)
		{
			initialiserCouche(coucheEntree, valeurBiais, tailleCoucheCachee);
		}
		for (int i = 0; i < tailleCoucheCachee; i++)
		{
			initialiserCouche(coucheCachee, valeurBiais, tailleCoucheSortie);
		}
		for (int i = 0; i < tailleCoucheSortie; i++)
		{
			initialiserCouche(coucheSortie, valeurBiais, 0);
		}
		initialiserBiais(coucheCachee, valeurBiais, tailleCoucheCachee);
		initialiserBiais(coucheSortie, valeurBiais, tailleCoucheSortie);
		coucheCachee.entrees = coucheEntree.neurones;
		coucheSortie.entrees = coucheCachee.neurones;
	}
	/**
	 * Sauvegarde le contenu du reseau actuel dans un fichier dont le nom est place en parametre. 
	 * @param name (String) Nom du fichier de sauvegarde. 
	 */
	/*
	 * Le fait d'avoir creer les methodes toString pour la classe couche et neurone 
	 * permet de les rendre serializable et
	 * d'écrire directement ces objets dans un fichier. 
	 */
	public void sauvegarder(String name)
	{
		try
		{
		FileOutputStream saveFile = new FileOutputStream(name);
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		save.writeObject(coucheEntree);
		save.writeObject(coucheCachee);
		save.writeObject(coucheSortie);
		save.writeObject(hauteur);	
		save.writeObject(largeur);	
		save.close();
		}
		catch(Exception exc)
		{
			exc.printStackTrace(); 
		}
	}
	/**
	 * Charge le contenu du reseau contenu dans un fichier de sauvegarde dont le nom est place en parametre. 
	 * @param name (String) Nom du fichier de sauvegarde. 
	 * @return Vrai si l'ouverture a reussi, faux sinon (pas de fichier).
	 */
	public boolean charger(String name)
	{
		File fichier = new File(name);
		if (fichier.exists())
		{
			try
			{
				FileInputStream saveFile = new FileInputStream(name);
				ObjectInputStream save = new ObjectInputStream(saveFile);
				coucheEntree = (couche) save.readObject();
				coucheCachee = (couche) save.readObject();
				coucheSortie = (couche) save.readObject();
				hauteur = (int) save.readObject();
				largeur = (int) save.readObject();
				save.close(); 
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
			initialiserSorties(coucheSortie.neurones.size());
			return true;
		}
		else return false;
	}
	/**
	 * Methode d'apprentissage pour l'image courante. 
	 * @param theorique (Double) Valeur theorique que l'on souhaite obtenir. 
	 */
	/*
	 * Charge l'image courante et ouvre le fichier de sauvegarde correspond s'il existe,
	 * en cree un sinon, 
	 * initialise la couche d'entree avec la valeur des pixels de l'image,
	 * determine les sorties reelles, 
	 * ameliore les poids tant que l'erreur obtenue est trop grande,
	 * sauvegarde le tout. 
	 */
	public void apprentissage(double theorique)
	{
		ImagePlus image = IJ.getImage();
		ImageProcessor im  = image.getProcessor();
		int lgr = image.getWidth();
		int haut = image.getHeight();
		String nomFichier = "sauvegarde"+Integer.toString(haut)+"x"+Integer.toString(lgr);
		if (!charger(nomFichier))
		{
			initialiser(haut, lgr);
			initialiserSorties(coucheSortie.neurones.size());
		}
		for (int i = 0; i < sortieTheorique.size(); i++)
		{
			if (i == (int) theorique)
			{
				sortieTheorique.set(i, (double) 1);
			}
			else
				sortieTheorique.set(i, (double) 0);
		}
		double eps = 0.5; 
		int pixels[] = (int[]) im.getPixels();
		nouvelleImage(pixels);
		mettreAJour();
		while (!estCorrect())
		{
			ameliorer(eps);
		}
		sauvegarder(nomFichier);
	}
	/**
	 * Affiche la valeur des couches de sorties pour determiner la classe de l'objet de l'image courant. 
	 */
	public void estPhytolithe()
	{
		ImagePlus image = IJ.getImage();
		int lgr = image.getWidth();
		int haut = image.getHeight();
		String nomFichier = "sauvegarde"+Integer.toString(haut)+"x"+Integer.toString(lgr);
		ImageProcessor im  = image.getProcessor();
		if (charger(nomFichier))
		{
			int pixels[] = (int[]) im.getPixels();
			nouvelleImage(pixels);
			mettreAJour();
			afficherCoucheSortie();
		}
		else
			IJ.showMessage("Aucune donnée pour ce format.");
	}
	/**
	 * Met a jour la couche d'entree avec la valeur des pixels de la nouvelle image. 
	 * @param pixels (Int[]) Pixels de l'image courante. 
	 */
	public void nouvelleImage(int pixels[]) 
	{ 
		for (int i = 0; i < coucheEntree.neurones.size(); i++)
		{
			coucheEntree.neurones.get(i).setValeur((double) pixels[i]);
		}
	}
	/**
	 * Ameliore le poids des differents synapses de chaque couche du reseau de neurones. 
	 * @param eps (Double) Valeur epsilon permettant d'ajuster la modification des poids. 
	 */
	public void ameliorer(double eps)
	{
		ArrayList <Double> deltaCoucheCachee = new ArrayList <Double>();
		ArrayList <Double> deltaCoucheSortie = new ArrayList <Double>();
		double valeurNeurone;
		double valeurPoids;
		double delta;
		double nouveauPoids;
		for (int i = 0; i < coucheSortie.neurones.size(); i++)
		{
			deltaCoucheSortie.add(sortieActuelle.get(i)-sortieTheorique.get(i));
		}
		for (int i = 0; i < coucheCachee.neurones.size(); i++)
		{
			for (int j = 0; j < coucheCachee.neurones.get(i).synapses.size(); j++) // calcule delta de la couche cachée + le nouveau poids des neurones de la couche cachée
			{
				valeurNeurone = coucheCachee.neurones.get(i).valeur;
				valeurPoids = coucheCachee.neurones.get(i).synapses.get(j);
				delta = valeurNeurone * (1-valeurNeurone) * valeurPoids * deltaCoucheSortie.get(j);
				deltaCoucheCachee.add(delta);
				nouveauPoids = valeurPoids+eps*deltaCoucheSortie.get(j)*valeurNeurone;
				coucheCachee.neurones.get(i).synapses.set(j, nouveauPoids);
			}
		}
		for (int i = 0; i < coucheEntree.neurones.size(); i++)
		{
			for (int j = 0; j < coucheEntree.neurones.get(i).synapses.size(); j++)
			{
				valeurNeurone = coucheEntree.neurones.get(i).valeur;
				valeurPoids = coucheEntree.neurones.get(i).synapses.get(j);
				nouveauPoids = valeurPoids+eps*deltaCoucheCachee.get(j)*valeurNeurone;
				coucheEntree.neurones.get(i).synapses.set(j, nouveauPoids);
			}
		}
		double valeurBiais;
		double valeurPoidsBiais;
		for (int i = 0; i < coucheCachee.neurones.size(); i++)
		{
			valeurBiais = coucheCachee.biais.valeur;
			valeurPoidsBiais = coucheCachee.biais.synapses.get(i);
			nouveauPoids = valeurPoidsBiais+eps*deltaCoucheCachee.get(i)*valeurBiais;
			coucheCachee.biais.synapses.set(i, nouveauPoids);
		}
		for (int i = 0; i < coucheSortie.neurones.size(); i++)
		{
			valeurBiais = coucheSortie.biais.valeur;
			valeurPoidsBiais = coucheSortie.biais.synapses.get(i);
			nouveauPoids = valeurPoidsBiais+eps*deltaCoucheSortie.get(i)*valeurBiais;
			coucheSortie.biais.synapses.set(i, nouveauPoids);
		}
		mettreAJour();
	}
	/**
	 * Calcule la valeur des differentes couches avec les nouvelles valeur de la couche d'entree. 
	 */
	public void mettreAJour()
	{
		coucheCachee.entrees = coucheEntree.neurones;
		coucheCachee.calculerSortie();
		coucheSortie.entrees = coucheCachee.neurones;
		coucheSortie.calculerSortie();
		for (int i = 0; i < coucheSortie.neurones.size(); i++)
		{
			sortieActuelle.set(i, coucheSortie.neurones.get(i).valeur);
		}
	}
	/**
	 * Determine si le reseau est correct pour une image donnee. 
	 * @return (Booleen) Vrai si la difference entre les sorties reelles et theoriques est inferieure a l'erreur toleree, faux sinon. 
	 */
	public boolean estCorrect()
	{
		for (int i = 0; i < sortieActuelle.size(); i++) 
		{
			if (Math.abs(sortieActuelle.get(i)-sortieTheorique.get(i)) > erreurToleree)
			{
				return false;
			}
		}
		return true;
	}
}