package ReseauDeNeurones;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Random;

/**
 * Classe permettant de creer un neurone et de generer des valeurs (aleatoires ou non) pour ses synapses. 
 * @author e1502316 Nicola Wilser
 *
 */

public class neurone implements Serializable{
		private static final long serialVersionUID = 1L;
		/**
		 * Valeur du neurone. 
		 */
		/*
		 * Par exemple pour la premiere couche il s'agit des pixels de l'image. 
		 */
		public double valeur; 
		/**
		 * Tableau contenant les synapses des neurones. Il s'agit des poids du neurone relatif aux neurones de la couche suivante aux quels il est relie. 
		 */
		/*
		 * La taille de l'ArrayList est la meme que celle de la couche suivante. S'il s'agit de la couche de sortie, il n'y en a pas. 
		 * Par exemple synapses.elementAt(3) contient la valeur du poids entre le neurone courant et le 3ème neurone de la couche suivante. 
		 */
		public ArrayList <Double> synapses; 
		/**
		 * Constructeur qui initialise la valeur du neurone a 0. 
		 */
		public neurone()
		{
			synapses = new ArrayList <Double>();
			valeur = 0;
		}
		/**
		 * Initialise les poids des synapses de maniere aleatoire. 
		 * @param nbPoids (Entier) Nombre de poids que l'on souhaite creer. 
		 */
		public void genererPoids(int nbPoids)
		{
			Random r = new Random();
			for (int i = 0; i < nbPoids; i++)
			{
				synapses.add(r.nextGaussian());
			}
		}
		/**
		 * Initialise les poids des synapses avec une valeur placee en parametre. 
		 * @param nbPoids (Entier) Nombre de poids que l'on souhaite creer. 
		 * @param valPoids (Double) Valeur des poids que l'on souhaite mettre. 
		 */
		public void genererPoids(int nbPoids, double valPoids)
		{
			for (int i = 0; i < nbPoids; i++)
			{
				synapses.add(valPoids);
			}
		}
		/**
		 * Renvoie un String de l'objet courant. 
		 */
		@Override
		public String toString() 
		{
			String str = new String();
			str = "";
			str += "Valeur = "+Double.toString(valeur);
			str += " | Poids : "+synapses.toString()+" | fin neurone.";
			return str;
		}
		/**
		 * Modifie la valeur du neurone. 
		 * @param valeur (Double) Valeur que l'on souhaite mettre pour le neurone. 
		 */
		public void setValeur(double valeur) {
			this.valeur = valeur;
		}
		/**
		 * Modifie le poids d'une synapse. 
		 * @param indice (Entier) Indice de la synapse pour la quelle on souhaite modifier le poids. 
		 * @param poids (Double) Valeur du poids que l'on souhaite mettre. 
		 */
		public void setPoids(int indice, double poids) {
			synapses.set(indice, poids);
		}
}
