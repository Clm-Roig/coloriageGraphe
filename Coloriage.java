import java.util.ArrayList;

public class Coloriage {
	// Sommets et Arretes du graphe
	static Sommet A = new Sommet("A");
	static Sommet B = new Sommet("B");
	static Sommet C = new Sommet("C");
	static Arrete ab = new Arrete(A,B,true);
	static Arrete bc = new Arrete(B,C,false);

	/* -------------------------- */
	/* --------- SOMMET --------- */
	/* -------------------------- */
	public static class Sommet {
		String nom;
		String couleur;

		public Sommet(String n) { this.nom = n; this.couleur = "void"; }

		public String toString() {
			return "" + nom + "|" + couleur;
		}
	}

	/* -------------------------- */
	/* --------- ARRETE --------- */
	/* -------------------------- */
	public static class Arrete {
		boolean isPref;
		Sommet s1;
		Sommet s2;

		public Arrete(Sommet s1, Sommet s2, boolean pref){this.s1 = s1; this.s2 = s2; this.isPref = pref;}

		public String toString() {
			return "" + s1.toString() + "--"+ isPref +"--" + s2.toString();
		}
	}


	/* -------------------------- */
	/* --------- GRAPHE --------- */
	/* -------------------------- */
	public static class Graphe {
		ArrayList<Sommet> sommets = new ArrayList<Sommet>();
		ArrayList<Arrete> arretes = new ArrayList<Arrete>();
		ArrayList<String> couleurs = new ArrayList<String>();

		public Graphe() {
			sommets.add(A); sommets.add(B); sommets.add(C);
			arretes.add(ab); arretes.add(bc);
			couleurs.add("RED"); couleurs.add("GREEN"); couleurs.add("BLUE") ; couleurs.add("YELLOW");
		}

		public String toString() {
			String res;
			res = "Sommets : " + sommets.get(0).toString() + " , " + sommets.get(1).toString() + " , " + sommets.get(2).toString();
			res += "\nArretes : " + arretes.get(0).toString() + " , " + arretes.get(1).toString();
			return res;
		}

		// Renvoie un sommet du graphe trivialement coloriable s'il existe, sinon null
		public Sommet somTrivial(int nbCoul) {
			Sommet res = null;

			for(Sommet s : sommets) {

				// Comptage du nombre d'arretes dans lesquelles le sommet est présent
				int nbApparition = 0;

				for(Arrete ar : arretes) {
					if(ar.s1 == s || ar.s2 == s){
						nbApparition++;
					}
				}

				if(nbApparition < nbCoul) {
					res = s;
					break;
				}
			}

			return res;
		}

		// Colorie les sommets du graphe
		public void colorier(Graphe g,int nbCoul) {
			Sommet s = somTrivial(nbCoul);
			if(s != null) {
				System.out.println("Sommet " + s);

				this.sommets.remove(s);
				colorier(this, nbCoul);

				// Coloriage de s
				ArrayList<String> couleursProches = new ArrayList<String>();
				for(Arrete ar : arretes) {
					// s dans l'arrête ?
					if(ar.s1 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {
							System.out.println(ar.toString() + " pref, on est sur s1");
							s.couleur = ar.s2.couleur;
						}
						else {
							couleursProches.add(ar.s2.couleur);
						}
					}
					else if (ar.s2 == s) {
						// Arrête de préférence ?
						if(ar.isPref) {
							System.out.println(ar.toString() + " pref, on est sur s2");
							s.couleur = ar.s1.couleur;
						}
						else {
							couleursProches.add(ar.s1.couleur);
						}
					}
				}

				// s n'apparaît dans aucune arrête de préférence et on connaît les couleurs proches
				// Il prend une des couleurs autres que les proches
				if(s.couleur == "void") {
					ArrayList<String> listeCouleursGraphe = new ArrayList<String>(this.couleurs);
					listeCouleursGraphe.removeAll(couleursProches);
					s.couleur = listeCouleursGraphe.get(0);
				}

			}

			/* Coloriage optimiste
				if(exist(sommetTrivialColoriable s)) {
					colorier(G / s)
					s.couleur = une couleur dispo
				}
				else if(exist(unSommetDansGs)){
					colorier(G / s)
					if(pasDeCouleurDispo()) {
						spiller(s)
					}
				}
			*/

		}


	}




	public static void main(String[] args) {
		// Graphe déjà saisi dans le code
		Graphe graphe = new Graphe();
		System.out.println(graphe.toString());
		graphe.colorier(graphe,3);
		System.out.println(A + " , " + B + " , " + C);


	}
}
