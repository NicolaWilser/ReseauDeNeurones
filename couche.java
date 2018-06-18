package ReseauDeNeurones;
import java.util.ArrayList;
import java.io.Serializable;
import java.lang.Math;

/**
 * Classe permettant de creer une couche du reseau de neurone et d'effectuer les calculs des sorties. 
 * @author e1502316 Nicola Wilser
 * 
 */

public class couche implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * Biais de la couche courante. Il s'agit d'un neurone normal ayant comme valeur de neurone toujours 1.
	 */
	public neurone biais; 
	/**
	 * Tableau de neurones des entrees de la couche actuelle. 
	 */
	public ArrayList <neurone> entrees; 
	/**
	 * Tableau des neurones de la couche actuelle. 
	 */
	public ArrayList <neurone> neurones;
	/**
	 * Renvoie un String de l'objet courant. 
	 */
	@Override
	public String toString() 
	{
		String str = new String();
		str = "Entrées : "+entrees.toString()+" | Neurones : "+neurones.toString()+" | Biais = "+biais.toString()+" | fin couche.";
		return str;
	}
	/**
	 * Constructeur par defaut qui initialise la couche comme etant vide. 
	 */
	public couche()
	{
		this.biais = new neurone();
		this.entrees = new ArrayList <neurone>();
		this.neurones = new ArrayList <neurone>();
	}
	/**
	 * Fonction Sigmoid qui calcule et renvoie la valeur pour un x donne en parametre. 
	 * @param x (Double) Valeur pour la quelle on souhaite calculer la fonction Sigmoid. 
	 * @return (Double) Valeur de la fonction Sigmoid. 
	 */
	public double sigmoid(double x)
	{
		return (1.0)/(1.0+Math.exp(-x));
	}
	/**
	 * Calcule la sortie des neurones de la couche actuelle. 
	 */
	/*
	 * La sortie est calculee en faisant la somme ponderee des neurones. 
	 * On calcule ensuite la fonction Sigmoid pour cette somme,
	 * et c'est ca qui correspond a la sortie. 
	 */
	public void calculerSortie()
	{ 
		double b;
		double sommePonderee;
		for (int i = 0; i < neurones.size(); i++) 
		{
			b = this.biais.synapses.get(i);
			sommePonderee = 0;				
			for (int j = 0; j < this.entrees.size(); j++) 
			{
				sommePonderee+= this.entrees.get(j).valeur*entrees.get(j).synapses.get(i);
			}
			double x = sommePonderee+b;
			this.neurones.get(i).setValeur(sigmoid(-x));
		}
	}
}