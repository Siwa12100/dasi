package td1.jeanico.patiment.console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.consultations.Prediction;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.TypeMedium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Utilisateur;
import td1.jeanico.patiment.metier.services.AuthService;
import td1.jeanico.patiment.metier.services.ClientService;
import td1.jeanico.patiment.metier.services.ConsultationService;
import td1.jeanico.patiment.metier.services.MediumService;
import td1.jeanico.patiment.metier.services.PredictionService;
import td1.jeanico.patiment.metier.services.StatistiqueService;
import td1.jeanico.patiment.outils.Saisie;

/**
 * IHM console principale de l'application PREDICT'IF.
 * Toute la navigation entre menus passe par cette classe.
 */
public class LanceurAppConsole {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a' HH'h'mm");

    private static final AuthService AUTH_SERVICE = new AuthService();
    private static final ClientService CLIENT_SERVICE = new ClientService();
    private static final MediumService MEDIUM_SERVICE = new MediumService();
    private static final ConsultationService CONSULTATION_SERVICE = new ConsultationService();
    private static final PredictionService PREDICTION_SERVICE = new PredictionService();
    private static final StatistiqueService STATISTIQUE_SERVICE = new StatistiqueService();

    private LanceurAppConsole() {
    }

    /**
     * Point d'entree de l'IHM console: affiche le menu principal en boucle.
     */
    public static void lancerApplication() {
        afficherBanniere();
        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            int choix = Saisie.lireInteger("👉 Votre choix :", Arrays.asList(1, 2, 3, 4));
            switch (choix) {
                case 1 -> menuConnexionClient();
                case 2 -> menuConnexionEmploye();
                case 3 -> menuInscriptionClient();
                case 4 -> continuer = false;
                default -> {
                }
            }
        }
        System.out.println();
        System.out.println("👋 Merci d'avoir utilise PREDICT'IF. A bientot !");
    }

    // ============================================================
    // 🏠 MENU PRINCIPAL
    // ============================================================

    /**
     * Affiche le bandeau d'accueil de l'application.
     */
    private static void afficherBanniere() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("   🔮  BIENVENUE CHEZ PREDICT'IF  🔮");
        System.out.println("   Votre destin n'attend que vous...");
        System.out.println("============================================================");
    }

    /**
     * Affiche les options du menu principal.
     */
    private static void afficherMenuPrincipal() {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("🏠 MENU PRINCIPAL");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1. 🔐 Se connecter (client)");
        System.out.println("  2. 👔 Se connecter (employe)");
        System.out.println("  3. ✍️  S'inscrire (nouveau client)");
        System.out.println("  4. 🚪 Quitter");
    }

    // ============================================================
    // 🔐 AUTHENTIFICATION
    // ============================================================

    /**
     * Demande mail/mot de passe et oriente vers l'espace client si valide.
     */
    private static void menuConnexionClient() {
        System.out.println();
        System.out.println("🔐 Connexion client");
        String mail = Saisie.lireChaine("✉️  Mail :");
        String motDePasse = Saisie.lireChaine("🔑 Mot de passe :");
        Utilisateur utilisateur = AUTH_SERVICE.authentifier(mail, motDePasse);
        if (utilisateur instanceof Client client) {
            System.out.println("✅ Connexion reussie. Bonjour " + client.getPrenom() + " !");
            menuClient(client);
        } else {
            System.out.println("❌ Identifiants invalides ou compte non client.");
            Saisie.pause();
        }
    }

    /**
     * Demande mail/mot de passe et oriente vers l'espace employe si valide.
     */
    private static void menuConnexionEmploye() {
        System.out.println();
        System.out.println("🔐 Connexion employe");
        String mail = Saisie.lireChaine("✉️  Mail :");
        String motDePasse = Saisie.lireChaine("🔑 Mot de passe :");
        Utilisateur utilisateur = AUTH_SERVICE.authentifier(mail, motDePasse);
        if (utilisateur instanceof Employe employe) {
            System.out.println("✅ Connexion reussie. Bonjour " + employe.getPrenom() + " !");
            menuEmploye(employe);
        } else {
            System.out.println("❌ Identifiants invalides ou compte non employe.");
            Saisie.pause();
        }
    }

    // ============================================================
    // ✍️ INSCRIPTION CLIENT
    // ============================================================

    /**
     * Collecte les informations d'inscription et delegue au service client.
     */
    private static void menuInscriptionClient() {
        System.out.println();
        System.out.println("✍️  Inscription d'un nouveau client");

        String nom = Saisie.lireChaine("🧾 Nom :");
        String prenom = Saisie.lireChaine("🧾 Prenom :");
        String mail = Saisie.lireChaine("✉️  Mail :");
        String motDePasse = Saisie.lireChaine("🔑 Mot de passe :");
        String telephone = Saisie.lireChaine("📞 Telephone :");
        Genre genre = lireGenre();
        LocalDate dateNaissance = lireDate("🎂 Date de naissance (jj/mm/aaaa) :");
        Adresse adresse = lireAdresseInscription();

        Client client = new Client(nom, prenom, mail, motDePasse, telephone, genre, adresse, dateNaissance);
        boolean inscrit = CLIENT_SERVICE.inscrire(client);
        if (inscrit) {
            System.out.println("🎉 Inscription reussie. Un mail de confirmation vous a ete envoye.");
        } else {
            System.out.println("❌ Inscription refusee (donnees invalides, adresse introuvable ou mail deja utilise).");
        }
        Saisie.pause();
    }

    // ============================================================
    // 👤 ESPACE CLIENT
    // ============================================================

    /**
     * Menu interactif accessible apres connexion d'un client.
     */
    private static void menuClient(Client client) {
        boolean continuer = true;
        while (continuer) {
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("👤 ESPACE CLIENT - " + client.getPrenom() + " " + client.getNom());
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. 🪪 Voir mon profil");
            System.out.println("  2. 🌙 Voir mon profil astral");
            System.out.println("  3. 🧙 Voir la liste des mediums");
            System.out.println("  4. 🔎 Filtrer les mediums par type");
            System.out.println("  5. 📞 Demander une consultation");
            System.out.println("  6. 📜 Voir mon historique de consultations");
            System.out.println("  7. 🚪 Se deconnecter");

            int choix = Saisie.lireInteger("👉 Votre choix :", Arrays.asList(1, 2, 3, 4, 5, 6, 7));
            switch (choix) {
                case 1 -> afficherProfilClient(client);
                case 2 -> afficherProfilAstralClient(client);
                case 3 -> afficherListeMediums(MEDIUM_SERVICE.listerMediums());
                case 4 -> menuFiltrerMediums();
                case 5 -> demanderConsultation(client);
                case 6 -> afficherHistoriqueClient(client);
                case 7 -> continuer = false;
                default -> {
                }
            }
        }
    }

    /**
     * Affiche les informations principales du client connecte.
     */
    private static void afficherProfilClient(Client client) {
        Client frais = CLIENT_SERVICE.consulterProfilClient(client);
        if (frais == null) {
            System.out.println("❌ Profil introuvable.");
            Saisie.pause();
            return;
        }
        System.out.println();
        System.out.println("🪪 Mon profil");
        System.out.println("  Nom        : " + frais.getNom());
        System.out.println("  Prenom     : " + frais.getPrenom());
        System.out.println("  Mail       : " + frais.getMail());
        System.out.println("  Telephone  : " + frais.getTelephone());
        System.out.println("  Genre      : " + frais.getGenre());
        System.out.println("  Naissance  : " + (frais.getDateNaissance() == null ? "?" : frais.getDateNaissance().format(FORMAT_DATE)));
        System.out.println("  Adresse    : " + formaterAdresse(frais.getAdresse()));
        Saisie.pause();
    }

    /**
     * Recupere et affiche le profil astral (le calcule au besoin via AstroNet).
     */
    private static void afficherProfilAstralClient(Client client) {
        ProfilAstral profil = CLIENT_SERVICE.consulterProfilAstral(client);
        System.out.println();
        System.out.println("🌙 Mon profil astral");
        if (profil == null) {
            System.out.println("  ❌ Profil astral indisponible.");
        } else {
            System.out.println("  ✨ Signe du zodiaque : " + profil.getSigneZodiac());
            System.out.println("  🐉 Signe chinois     : " + profil.getSigneChinois());
            System.out.println("  🐾 Animal totem      : " + profil.getAnimalTotem());
            System.out.println("  🎨 Couleur bonheur   : " + profil.getCouleurBonheur());
        }
        Saisie.pause();
    }

    /**
     * Demande un type de medium puis affiche la liste filtree.
     */
    private static void menuFiltrerMediums() {
        List<TypeMedium> types = MEDIUM_SERVICE.listerTypesMedium();
        System.out.println();
        System.out.println("🔎 Filtrer les mediums par type");
        List<Integer> choix = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + types.get(i));
            choix.add(i + 1);
        }
        int index = Saisie.lireInteger("👉 Votre choix :", choix);
        afficherListeMediums(MEDIUM_SERVICE.listerMediums(types.get(index - 1)));
    }

    /**
     * Affiche une liste de mediums sous forme tabulaire compacte.
     */
    private static void afficherListeMediums(List<Medium> mediums) {
        System.out.println();
        System.out.println("🧙 Liste des mediums (" + mediums.size() + ")");
        if (mediums.isEmpty()) {
            System.out.println("  (aucun medium)");
        } else {
            for (Medium medium : mediums) {
                System.out.println("  - [#" + medium.getId() + "] "
                        + medium.getDenomination()
                        + " (" + medium.getClass().getSimpleName() + ", " + medium.getGenre() + ")");
                if (medium.getPresentation() != null && !medium.getPresentation().isBlank()) {
                    System.out.println("      " + medium.getPresentation());
                }
            }
        }
        Saisie.pause();
    }

    /**
     * Permet au client de choisir un medium et de declencher la consultation.
     */
    private static void demanderConsultation(Client client) {
        List<Medium> mediums = MEDIUM_SERVICE.listerMediums();
        if (mediums.isEmpty()) {
            System.out.println("❌ Aucun medium disponible.");
            Saisie.pause();
            return;
        }
        System.out.println();
        System.out.println("📞 Choisissez votre medium :");
        List<Integer> choix = new ArrayList<>();
        for (int i = 0; i < mediums.size(); i++) {
            Medium medium = mediums.get(i);
            System.out.println("  " + (i + 1) + ". " + medium.getDenomination()
                    + " (" + medium.getClass().getSimpleName() + ")");
            choix.add(i + 1);
        }
        int index = Saisie.lireInteger("👉 Votre choix :", choix);
        Medium choisi = mediums.get(index - 1);
        Consultation result = CONSULTATION_SERVICE.demanderConsultation(client, choisi);
        if (result != null) {
            System.out.println("✅ Consultation demandee. Un de nos employes incarnera bientot " + choisi.getDenomination() + ".");
        } else {
            System.out.println("❌ Aucun employe disponible pour le moment, reessayez plus tard.");
        }
        Saisie.pause();
    }

    /**
     * Affiche l'historique des consultations du client connecte.
     */
    private static void afficherHistoriqueClient(Client client) {
        List<Consultation> historique = CONSULTATION_SERVICE.consulterHistoriqueConsultations(client);
        System.out.println();
        System.out.println("📜 Historique des consultations (" + historique.size() + ")");
        if (historique.isEmpty()) {
            System.out.println("  (aucune consultation)");
        } else {
            for (Consultation consultation : historique) {
                System.out.println("  - " + consultation.getDate().format(FORMAT_DATE_HEURE)
                        + " | " + consultation.getMedium().getDenomination()
                        + " | " + (consultation.isEstTermine() ? "✅ terminee" : "⏳ en cours"));
                if (consultation.getCommentaire() != null && !consultation.getCommentaire().isBlank()) {
                    System.out.println("      💬 " + consultation.getCommentaire());
                }
            }
        }
        Saisie.pause();
    }

    // ============================================================
    // 👔 ESPACE EMPLOYE
    // ============================================================

    /**
     * Menu interactif accessible apres connexion d'un employe.
     */
    private static void menuEmploye(Employe employe) {
        boolean continuer = true;
        while (continuer) {
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("👔 ESPACE EMPLOYE - " + employe.getPrenom() + " " + employe.getNom());
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. 🎯 Voir ma consultation en cours");
            System.out.println("  2. 📣 Declarer que je suis pret");
            System.out.println("  3. 🔮 Generer une prediction");
            System.out.println("  4. ✅ Terminer la consultation");
            System.out.println("  5. 📊 Acceder aux statistiques");
            System.out.println("  6. 🚪 Se deconnecter");

            int choix = Saisie.lireInteger("👉 Votre choix :", Arrays.asList(1, 2, 3, 4, 5, 6));
            switch (choix) {
                case 1 -> afficherConsultationEnCours(employe);
                case 2 -> declarerPret(employe);
                case 3 -> genererPrediction(employe);
                case 4 -> terminerConsultation(employe);
                case 5 -> menuStatistiques();
                case 6 -> continuer = false;
                default -> {
                }
            }
        }
    }

    /**
     * Affiche la consultation actuellement affectee a l'employe.
     */
    private static void afficherConsultationEnCours(Employe employe) {
        Consultation consultation = CONSULTATION_SERVICE.consulterConsultationAffectee(employe);
        System.out.println();
        System.out.println("🎯 Ma consultation en cours");
        if (consultation == null) {
            System.out.println("  (aucune consultation active)");
        } else {
            afficherFicheConsultation(consultation);
        }
        Saisie.pause();
    }

    /**
     * Envoie la notification "pret" au client de la consultation active.
     */
    private static void declarerPret(Employe employe) {
        Consultation consultation = CONSULTATION_SERVICE.consulterConsultationAffectee(employe);
        if (consultation == null) {
            System.out.println("❌ Aucune consultation active.");
        } else {
            CONSULTATION_SERVICE.declarerPret(consultation);
            System.out.println("📣 Notification envoyee a " + consultation.getClient().getPrenom() + ".");
        }
        Saisie.pause();
    }

    /**
     * Demande trois scores et affiche la prediction generee par AstroNet.
     */
    private static void genererPrediction(Employe employe) {
        Consultation consultation = CONSULTATION_SERVICE.consulterConsultationAffectee(employe);
        if (consultation == null) {
            System.out.println("❌ Aucune consultation active.");
            Saisie.pause();
            return;
        }
        ProfilAstral profil = CLIENT_SERVICE.consulterProfilAstral(consultation.getClient());
        if (profil == null) {
            System.out.println("❌ Profil astral du client indisponible.");
            Saisie.pause();
            return;
        }
        List<Integer> scores = Arrays.asList(1, 2, 3, 4);
        int amour = Saisie.lireInteger("❤️  Score amour (1-4) :", scores);
        int sante = Saisie.lireInteger("💪 Score sante (1-4) :", scores);
        int travail = Saisie.lireInteger("💼 Score travail (1-4) :", scores);
        Prediction prediction = PREDICTION_SERVICE.demandeInspiration(profil, amour, sante, travail);
        System.out.println();
        if (prediction == null) {
            System.out.println("❌ Generation de prediction impossible.");
        } else {
            System.out.println("🔮 Prediction generee :");
            System.out.println("  ❤️  Amour   : " + prediction.getAmour());
            System.out.println("  💪 Sante   : " + prediction.getSante());
            System.out.println("  💼 Travail : " + prediction.getTravail());
        }
        Saisie.pause();
    }

    /**
     * Cloture la consultation active et libere l'employe.
     */
    private static void terminerConsultation(Employe employe) {
        Consultation consultation = CONSULTATION_SERVICE.consulterConsultationAffectee(employe);
        if (consultation == null) {
            System.out.println("❌ Aucune consultation active.");
            Saisie.pause();
            return;
        }
        afficherFicheConsultation(consultation);
        String commentaire = Saisie.lireChaine("💬 Votre commentaire de cloture :");
        CONSULTATION_SERVICE.terminerConsultation(consultation, commentaire);
        System.out.println("✅ Consultation terminee. Vous etes a nouveau disponible.");
        Saisie.pause();
    }

    // ============================================================
    // 📊 STATISTIQUES
    // ============================================================

    /**
     * Sous-menu accessible aux employes pour consulter les KPIs.
     */
    private static void menuStatistiques() {
        boolean continuer = true;
        while (continuer) {
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("📊 STATISTIQUES");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. 🧙 Consultations par medium");
            System.out.println("  2. 👥 Clients distincts par employe");
            System.out.println("  3. 🏆 Top mediums");
            System.out.println("  4. 🗺️  Repartition geographique des clients");
            System.out.println("  5. ↩️  Retour");

            int choix = Saisie.lireInteger("👉 Votre choix :", Arrays.asList(1, 2, 3, 4, 5));
            switch (choix) {
                case 1 -> afficherConsultationsParMedium();
                case 2 -> afficherClientsParEmploye();
                case 3 -> afficherTopMediums();
                case 4 -> afficherRepartitionGeographique();
                case 5 -> continuer = false;
                default -> {
                }
            }
        }
    }

    private static void afficherConsultationsParMedium() {
        Map<Medium, Integer> map = STATISTIQUE_SERVICE.listerNombreConsultationsParMedium();
        System.out.println();
        System.out.println("🧙 Consultations par medium :");
        map.forEach((medium, nb) -> System.out.println("  - " + medium.getDenomination() + " : " + nb));
        Saisie.pause();
    }

    private static void afficherClientsParEmploye() {
        Map<Employe, Integer> map = STATISTIQUE_SERVICE.listerRepartitionClientParEmploye();
        System.out.println();
        System.out.println("👥 Clients distincts par employe :");
        map.forEach((employe, nb) -> System.out.println("  - " + employe.getPrenom() + " " + employe.getNom() + " : " + nb));
        Saisie.pause();
    }

    private static void afficherTopMediums() {
        int nb = Saisie.lireInteger("🏆 Combien de mediums dans le top ?");
        List<Map<Medium, Integer>> top = STATISTIQUE_SERVICE.listerMediumsPopulaire(nb);
        System.out.println();
        System.out.println("🏆 Top " + nb + " mediums :");
        int rang = 1;
        for (Map<Medium, Integer> ligne : top) {
            for (Map.Entry<Medium, Integer> entry : ligne.entrySet()) {
                System.out.println("  " + rang + ". " + entry.getKey().getDenomination() + " (" + entry.getValue() + ")");
                rang++;
            }
        }
        Saisie.pause();
    }

    private static void afficherRepartitionGeographique() {
        Map<String, Integer> map = STATISTIQUE_SERVICE.listerRepartitionGeographiqueClients();
        System.out.println();
        System.out.println("🗺️  Repartition geographique (par departement) :");
        map.forEach((dept, nb) -> System.out.println("  - " + dept + " : " + nb));
        Saisie.pause();
    }

    // ============================================================
    // 🛠️ HELPERS DE SAISIE / AFFICHAGE
    // ============================================================

    /**
     * Affiche le contenu detaille d'une consultation.
     */
    private static void afficherFicheConsultation(Consultation consultation) {
        Client client = consultation.getClient();
        System.out.println("  📅 Date    : " + consultation.getDate().format(FORMAT_DATE_HEURE));
        System.out.println("  🧙 Medium  : " + consultation.getMedium().getDenomination());
        System.out.println("  👤 Client  : " + client.getPrenom() + " " + client.getNom() + " (" + client.getTelephone() + ")");
        System.out.println("  📌 Statut  : " + (consultation.isEstTermine() ? "terminee" : "en cours"));
    }

    /**
     * Demande au client de choisir son genre dans la liste autorisee.
     */
    private static Genre lireGenre() {
        Genre[] valeurs = Genre.values();
        System.out.println("🚻 Genre :");
        List<Integer> choix = new ArrayList<>();
        for (int i = 0; i < valeurs.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valeurs[i]);
            choix.add(i + 1);
        }
        int index = Saisie.lireInteger("👉 Votre choix :", choix);
        return valeurs[index - 1];
    }

    /**
     * Lit une date au format jj/mm/aaaa avec relance en cas d'erreur.
     */
    private static LocalDate lireDate(String invite) {
        while (true) {
            String saisie = Saisie.lireChaine(invite);
            try {
                return LocalDate.parse(saisie, FORMAT_DATE);
            } catch (DateTimeParseException ex) {
                System.out.println("/!\\ Format attendu : jj/mm/aaaa /!\\");
            }
        }
    }

    /**
     * Propose une adresse de test par defaut avant une saisie manuelle complete.
     */
    private static Adresse lireAdresseInscription() {
        System.out.println();
        System.out.println("🏠 Adresse d'inscription");
        System.out.println("  1. ⚡ Utiliser l'adresse de test par defaut");
        System.out.println("  2. ✍️  Saisir une adresse manuellement");
        int choix = Saisie.lireInteger("👉 Votre choix :", Arrays.asList(1, 2));
        if (choix == 1) {
            Adresse adresseParDefaut = adresseParDefaut();
            System.out.println("✅ Adresse de test selectionnee : " + formaterAdresse(adresseParDefaut));
            return adresseParDefaut;
        }
        return lireAdresse();
    }

    /**
     * Lit une adresse complete au clavier.
     */
    private static Adresse lireAdresse() {
        System.out.println("🏠 Adresse :");
        String numero = Saisie.lireChaine("  Numero de voie :");
        String voie = Saisie.lireChaine("  Nom de voie :");
        String codePostal = Saisie.lireChaine("  Code postal :");
        String codeDepartement = Saisie.lireChaine("  Code departement :");
        String ville = Saisie.lireChaine("  Ville :");
        return new Adresse(numero, voie, codePostal, codeDepartement, ville);
    }

    /**
     * Retourne une adresse simple et valide pour accelerer les tests manuels.
     */
    private static Adresse adresseParDefaut() {
        return new Adresse("67", "Impasse des Moissons", "01000", "01", "Saint Denis lès Bourg");
    }

    /**
     * Met en forme une adresse pour l'affichage.
     */
    private static String formaterAdresse(Adresse adresse) {
        if (adresse == null) {
            return "(non renseignee)";
        }
        return adresse.getNumeroDeVoie() + " " + adresse.getNomDeVoie()
                + ", " + adresse.getCodePostal() + " " + adresse.getVille()
                + " (" + adresse.getCodeDepartement() + ")";
    }
}
