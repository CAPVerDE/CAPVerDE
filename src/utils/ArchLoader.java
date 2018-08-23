package utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import architecture.Action;
import architecture.Component;
import architecture.Composition;
import architecture.Deduction;
import architecture.DeductionCapability;
import architecture.Dep;
import architecture.DependenceRelation;
import architecture.Equation;
import architecture.Term;
import architecture.Variable;
import properties.Property;
import properties.Property.PropertyType;
import architecture.Action.ActionType;
import architecture.Equation.Relation;
import architecture.Equation.Type;
import architecture.Term.Operator;
import architecture.Term.OperatorType;
import architecture.Term.TermType;
import architecture.Trust;
import gui.ArchitectureFunctions;
import gui.ArchitectureFunctions.CaseStudy;

/**
 * Class that helps loading the pre-existing case studies.
 */
public class ArchLoader {

	// class fields
	//########## Smart Home #################
	// Components
	private static Component SM = new Component("SM");
	private static Component MI = new Component("MI");
	private static Component Re = new Component("Re");
	private static Component HN = new Component("HN");
	private static Set<Component> cSet1 = Stream.of(SM, MI, Re, HN).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable readings = new Variable("readings");
	private static Variable k = new Variable("k");
	private static Variable bill = new Variable("bill");
	private static Variable pw = new Variable("pw");
	private static Variable secret = new Variable("secret");
	private static Variable encBill = new Variable("encBill");
	private static Variable encR = new Variable("encR");
	private static Variable ppd = new Variable("ppd");
	private static Set<Variable> vSet1 = Stream.of(readings, k, bill, pw, secret, encBill, encR, ppd).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termReadings = new Term(TermType.ATOM, readings, false);
	private static Term termK = new Term(TermType.ATOM, k, false);
	private static Term termBill = new Term(TermType.ATOM, bill, false);
	private static Term termPw = new Term(TermType.ATOM, pw, false);
	private static Term termSecret = new Term(TermType.ATOM, secret, false);
	private static Term termPpd = new Term(TermType.ATOM, ppd, false);
	private static Term termEncB = new Term(TermType.ATOM, encBill, false);
	private static Term termEncR = new Term(TermType.ATOM, encR, false);
	private static Term termBetaReadings = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "beta", termReadings, false);
	private static Term termEncReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Enc", termReadings, termK, false);
	private static Term termDecReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Dec", termEncR, termK, false);
	private static Term termEncBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Enc", termBill, termK, false);
	private static Term termDecBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Dec", termEncB, termK, false);
	private static Term termPhiReadings = new Term(
			TermType.COMPOSITION, OperatorType.TERTIARY, Operator.FUNC, "phi", termReadings, termBill, termPw, false);
	private static Term termPhiInvReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "phi^-1", termPpd, termPw, false);
	private static Term termPhiInvBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "phi^-1", termPpd, termPw, false);
	private static Set<Term> tSet1 = Stream.of(
			termReadings, termK, termBill, termPw, termSecret, termPpd, termEncB, termEncR,
			termBetaReadings, termEncReadings, termDecReadings, termEncBill, termDecBill,
			termPhiReadings, termPhiInvReadings, termPhiInvBill).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation encR_enc = new Equation(
			"encR_enc", Type.RELATION, Relation.EQUALITY, termEncR, termEncReadings);
	private static Equation bill_dec = new Equation(
			"bill_dec", Type.RELATION, Relation.EQUALITY, termBill, termDecBill);
	private static Equation ppd_phi = new Equation(
			"ppd_phi", Type.RELATION, Relation.EQUALITY, termPpd, termPhiReadings);
	private static Equation readings_dec = new Equation(
			"readings_dec", Type.RELATION, Relation.EQUALITY, termReadings, termDecReadings);
	private static Equation bill_beta = new Equation(
			"bill_beta", Type.RELATION, Relation.EQUALITY, termBill, termBetaReadings);
	private static Equation encBill_enc = new Equation(
			"encBill_enc", Type.RELATION, Relation.EQUALITY, termEncB, termEncBill);
	private static Equation readings_phiInv = new Equation(
			"readings_phiInv", Type.RELATION, Relation.EQUALITY, termReadings, termPhiInvReadings);
	private static Equation bill_phiInv = new Equation(
			"bill_phiInv", Type.RELATION, Relation.EQUALITY, termBill, termPhiInvBill);
	private static Set<Equation> eSet1 = Stream.of(
			encR_enc, bill_dec, ppd_phi, readings_dec, bill_beta, encBill_enc, readings_phiInv, bill_phiInv).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet1 = new LinkedHashSet<Trust>();
	// Compositions
	private static Set<Composition> composSet1 = new LinkedHashSet<Composition>();
	// Statements
	private static Set<architecture.Statement> stSet1 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasSM_readings = new Action(ActionType.HAS, SM, readings);
	private static Action hasSM_pw = new Action(ActionType.HAS, SM, pw);
	private static Action hasSM_k = new Action(ActionType.HAS, SM, k);
	private static Action hasMI_k = new Action(ActionType.HAS, MI, k);
	private static Action hasRe_pw = new Action(ActionType.HAS, Re, pw);
	private static Action hasRe_secret = new Action(ActionType.HAS, Re, secret);
	private static Action computeSM_encR = new Action(
			ActionType.COMPUTE, SM, encR_enc);
	private static Action computeSM_bill = new Action(
			ActionType.COMPUTE, SM, bill_dec);
	private static Action computeSM_ppd = new Action(
			ActionType.COMPUTE, SM, ppd_phi);
	private static Action computeMI_readings = new Action(
			ActionType.COMPUTE, MI, readings_dec);
	private static Action computeMI_bill = new Action(
			ActionType.COMPUTE, MI, bill_beta);
	private static Action computeMI_encBill = new Action(
			ActionType.COMPUTE, MI, encBill_enc);
	private static Action computeRe_readings = new Action(
			ActionType.COMPUTE, Re, readings_phiInv);
	private static Action computeRe_bill = new Action(
			ActionType.COMPUTE, Re, bill_phiInv);
	private static Action receiveHNSM1 = new Action(
			ActionType.RECEIVE, HN, SM, Collections.emptySet(), Set.of(encR));
	private static Action receiveSMHN = new Action(
			ActionType.RECEIVE, SM, HN, Collections.emptySet(), Set.of(encBill));
	private static Action receiveHNSM2 = new Action(
			ActionType.RECEIVE, HN, SM, Collections.emptySet(), Set.of(ppd));
	private static Action receiveMIHN = new Action(
			ActionType.RECEIVE, MI, HN, Collections.emptySet(), Set.of(encR));
	private static Action receiveHNMI = new Action(
			ActionType.RECEIVE, HN, MI, Collections.emptySet(), Set.of(encBill));
	private static Action receiveReHN = new Action(
			ActionType.RECEIVE, Re, HN, Collections.emptySet(), Set.of(ppd));
	private static Action checkRe = new Action(
			ActionType.CHECK, Re, Set.of(bill_beta));
	private static Set<Action> aSet1 = Stream.of(
			hasSM_readings, hasSM_pw, hasSM_k, hasMI_k, hasRe_pw, hasRe_secret, computeSM_encR, computeSM_bill,
			computeSM_ppd, computeMI_readings, computeMI_bill, computeMI_encBill, computeRe_readings,
			computeRe_bill, receiveHNSM1, receiveSMHN, receiveHNSM2, receiveMIHN, receiveHNMI, receiveReHN, checkRe).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	private static Dep dep1 = new Dep(pw, Collections.emptySet(), 0.001);
	private static Dep dep2 = new Dep(readings, Set.of(encR), 0.00001);
	private static Dep dep3 = new Dep(readings, Set.of(ppd, pw), 1);
	private static Dep dep4 = new Dep(secret, Collections.emptySet(), 0.01);
	private static Dep dep5 = new Dep(pw, Set.of(secret), 1);
	private static Set<DependenceRelation> dSet1 = Stream.of(
			new DependenceRelation(HN, dep1), new DependenceRelation(HN, dep2),
			new DependenceRelation(HN, dep3), new DependenceRelation(HN, dep4),
			new DependenceRelation(HN, dep5)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static Variable varT = new Variable("t");
	private static Term termT = new Term(TermType.ATOM, varT, true);
	private static Variable varU = new Variable("u");
	private static Term termU = new Term(TermType.ATOM, varU, true);
	private static Variable varX = new Variable("x");
	private static Variable varY = new Variable("y");
	private static Term termX = new Term(TermType.ATOM, varX, true);
	private static Term termY = new Term(TermType.ATOM, varY, true);
	private static Equation dedEq2 = new Equation(
			"subst", Type.RELATION, Relation.EQUALITY, termT, termU);
	private static Equation dedEq3 = new Equation(
			"dedEq3", Type.RELATION, Relation.EQUALITY, termX, termY);
	private static Set<Equation> dedEqSet2 = Set.of(dedEq2, dedEq3);
	private static Deduction deduc4 = new Deduction(
			Deduction.Type.SUBST, dedEqSet2, dedEq2, "Substitution", 1);
	private static DeductionCapability dc_SM = new DeductionCapability(SM, Set.of(deduc4));
	private static DeductionCapability dc_MI = new DeductionCapability(MI, Set.of(deduc4));
	private static DeductionCapability dc_Re = new DeductionCapability(Re, Set.of(deduc4));
	private static DeductionCapability dc_HN = new DeductionCapability(HN, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet1 = Stream.of(dc_SM, dc_MI, dc_Re, dc_HN).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property statement1 = new Property(PropertyType.KNOWS, Re, (double)1, bill_beta);
	private static Property statement2_tmp = new Property(PropertyType.HAS, HN, 0.001, readings);
	private static Property statement2 = new Property(PropertyType.NEGATION, statement2_tmp);
	//TODO 1 more?
	private static Set<Property> pSet1 = Stream.of(statement1, statement2).collect(Collectors.toCollection(LinkedHashSet::new));

	//############## AccuWeather ###############
	// Components
	private static Component U = new Component("U");
	private static Component AW = new Component("AW");
	private static Component RM = new Component("RM");
	private static Set<Component> cSet2 = Stream.of(U, AW, RM).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable location = new Variable("location");
	private static Variable wifi_info = new Variable("wifi_info");
	private static Variable weather = new Variable("weather");
	private static Set<Variable> vSet2 = Stream.of(location, wifi_info, weather).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termLocation = new Term(TermType.ATOM, location, false);
	private static Term termWifi_info = new Term(TermType.ATOM, wifi_info, false);
	private static Term termWeather = new Term(TermType.ATOM, weather, false);
	private static Term termPhiWifi = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "phi", termWifi_info, false);
	private static Set<Term> tSet2 = Stream.of(
			termLocation, termWifi_info, termWeather, termPhiWifi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation location_phi = new Equation(
			"location_phi", Type.RELATION, Relation.EQUALITY, termLocation, termPhiWifi);
	private static Set<Equation> eSet2 = Stream.of(location_phi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet2 = new LinkedHashSet<Trust>();
	// Compositions
	private static Set<Composition> composSet2 = new LinkedHashSet<Composition>();
	// Statements
	private static Set<architecture.Statement> stSet2 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasU_location = new Action(ActionType.HAS, U, location);
	private static Action hasU_wifi_info = new Action(ActionType.HAS, U, wifi_info);
	private static Action hasAW_weather = new Action(ActionType.HAS, AW, weather);
	private static Action computeRM_location = new Action(
			ActionType.COMPUTE, RM, location_phi);
	private static Action receiveAWU = new Action(
			ActionType.RECEIVE, AW, U, Collections.emptySet(), Set.of(wifi_info));
	private static Action receiveRMAW = new Action(
			ActionType.RECEIVE, RM, AW, Collections.emptySet(), Set.of(wifi_info));
	private static Action receiveUAW = new Action(
			ActionType.RECEIVE, U, AW, Collections.emptySet(), Set.of(weather));
	private static Set<Action> aSet2 = Stream.of(
			hasU_location, hasU_wifi_info, hasAW_weather, computeRM_location, receiveAWU, receiveRMAW, receiveUAW).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	private static Dep dep = new Dep(location, Set.of(wifi_info), 0.5);
	private static Set<DependenceRelation> dSet2 = Stream.of(
			new DependenceRelation(AW, dep), new DependenceRelation(RM, dep)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static DeductionCapability dc_U = new DeductionCapability(U, Set.of(deduc4));
	private static DeductionCapability dc_AW = new DeductionCapability(AW, Set.of(deduc4));
	private static DeductionCapability dc_RM = new DeductionCapability(RM, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet2 = Stream.of(dc_U, dc_AW, dc_RM).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property property_accuracy = new Property(PropertyType.HAS, U, (double)1, weather);
	private static Property property_tmp = new Property(PropertyType.HAS, AW, 0.001, location);
	private static Property property_dataMinimisation = new Property(PropertyType.NEGATION, property_tmp);
	private static Property property_dataMinimisation2 = new Property(PropertyType.NOTSHARED, AW, wifi_info);
	//TODO 1 more?
	private static Set<Property> pSet2 = Stream.of(property_accuracy, property_dataMinimisation, property_dataMinimisation2).collect(Collectors.toCollection(LinkedHashSet::new));

	//########## Patient Data Register #################
	// Components
	private static Component M = new Component("M");
	private static Component CR = new Component("CR");
	private static Component MAi = new Component("MA", "i");
	private static Component SA = new Component("SA");
	private static Component MCi = new Component("MC", "i");
	private static Component Pj = new Component("P", "j");
	private static Component Rl = new Component("R", "l");
	private static Component MDik = new Component("MD", "ik");
	private static Component IDBi = new Component("IDB", "i");
	private static Component RDBi = new Component("RDB", "i");
	private static Set<Component> cSet3 = Stream.of(M, CR, MAi, SA, MCi, Pj, Rl, MDik, IDBi, RDBi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable cki = new Variable("ck", "i");
	private static Variable iki = new Variable("ik", "i");
	private static Variable pukik = new Variable("puk", "ik");
	private static Variable eckik = new Variable("eck", "ik");
	private static Variable eikik = new Variable("eik", "ik");
	private static Variable pDj = new Variable("pD", "j");
	private static Variable mDj = new Variable("mD", "j");
	private static Variable eukik = new Variable("euk", "ik");
	private static Variable pubMk = new Variable("pubMk");
	private static Variable privMk = new Variable("privMk");
	private static Variable pwkik = new Variable("pwk", "ik");
	private static Variable rkj = new Variable("rk", "j");
	private static Variable ukik = new Variable("uk", "ik");
	private static Variable epDj = new Variable("epD", "j");
	private static Variable emDj = new Variable("emD", "j");
	private static Variable cerkj = new Variable("cerk", "j");
	private static Variable merkj = new Variable("merk", "j");
	private static Variable amDj = new Variable("amD", "j");
	private static Variable stats = new Variable("stats");
	private static Set<Variable> vSet3 = Stream.of(cki, iki, pukik, eckik, eikik, pDj, mDj, eukik, pubMk, privMk, pwkik,
			rkj, ukik, epDj, emDj, cerkj, merkj, amDj, stats).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termCki = new Term(TermType.ATOM, cki, false);
	private static Term termIki = new Term(TermType.ATOM, iki, false);
	private static Term termPukik = new Term(TermType.ATOM, pukik, false);
	private static Term termEckik = new Term(TermType.ATOM, eckik, false);
	private static Term termEikik = new Term(TermType.ATOM, eikik, false);
	private static Term termPDj = new Term(TermType.ATOM, pDj, false);
	private static Term termMDj = new Term(TermType.ATOM, mDj, false);
	private static Term termEukik = new Term(TermType.ATOM, eukik, false);
	private static Term termPubMk = new Term(TermType.ATOM, pubMk, false);
	private static Term termPrivMk = new Term(TermType.ATOM, privMk, false);
	private static Term termPwkik = new Term(TermType.ATOM, pwkik, false);
	private static Term termRkj = new Term(TermType.ATOM, rkj, false);
	private static Term termUkik = new Term(TermType.ATOM, ukik, false);
	private static Term termEpDj = new Term(TermType.ATOM, epDj, false);
	private static Term termEmDj = new Term(TermType.ATOM, emDj, false);
	private static Term termCerkj = new Term(TermType.ATOM, cerkj, false);
	private static Term termMerkj = new Term(TermType.ATOM, merkj, false);
	private static Term termAmDj = new Term(TermType.ATOM, amDj, false);
	private static Term termStats = new Term(TermType.ATOM, stats, false);
	private static Term termEval = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "eval", termAmDj, false);
	private static Term termDecMerkj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termMerkj, termPrivMk, false);
	private static Term termDecEmDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEmDj, termDecMerkj, false);
	private static Term termAnon = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "anon", termDecEmDj, false);
	private static Term termEncIki = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termIki, termPukik, false);
	private static Term termEncCki = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termCki, termPukik, false);
	private static Term termDecEukik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEukik, termPwkik, false);
	private static Term termDecEckik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEckik, termUkik, false);
	private static Term termDecEikik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEikik, termUkik, false);
	private static Term termEncPDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termPDj, termIki, false);
	private static Term termEncMDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termMDj, termRkj, false);
	private static Term termEncRkj1 = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termRkj, termCki, false);
	private static Term termEncRkj2 = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termRkj, termPubMk, false);
	private static Set<Term> tSet3 = Stream.of(
			termCki, termIki, termPukik, termEckik, termEikik, termPDj, termMDj, termEukik, termPubMk, termPrivMk,
			termPwkik, termRkj, termUkik, termEpDj, termEmDj, termCerkj, termMerkj, termAmDj, termStats, termEval,
			termDecEmDj, termAnon, termEncIki, termEncCki, termDecEukik, termDecEckik, termDecEikik, termEncPDj,
			termEncMDj, termEncRkj1, termEncRkj2, termDecMerkj).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation eck_enc = new Equation(
			"eck_enc", Type.RELATION, Relation.EQUALITY, termEckik, termEncCki);
	private static Equation eik_enc = new Equation(
			"eik_enc", Type.RELATION, Relation.EQUALITY, termEikik, termEncIki);
	private static Equation uk_dec = new Equation(
			"uk_dec", Type.RELATION, Relation.EQUALITY, termUkik, termDecEukik);
	private static Equation ck_dec = new Equation(
			"ck_dec", Type.RELATION, Relation.EQUALITY, termCki, termDecEckik);
	private static Equation ik_dec = new Equation(
			"ik_dec", Type.RELATION, Relation.EQUALITY, termIki, termDecEikik);
	private static Equation emD_enc = new Equation(
			"emD_enc", Type.RELATION, Relation.EQUALITY, termEmDj, termEncMDj);
	private static Equation epD_enc = new Equation(
			"epD_enc", Type.RELATION, Relation.EQUALITY, termEpDj, termEncPDj);
	private static Equation cerk_enc = new Equation(
			"cerk_enc", Type.RELATION, Relation.EQUALITY, termCerkj, termEncRkj1);
	private static Equation merk_enc = new Equation(
			"merk_enc", Type.RELATION, Relation.EQUALITY, termMerkj, termEncRkj2);
	private static Equation amD_anon = new Equation(
			"amD_anon", Type.RELATION, Relation.EQUALITY, termAmDj, termAnon);
	private static Equation stats_eval = new Equation(
			"stats_eval", Type.RELATION, Relation.EQUALITY, termStats, termEval);
	private static Set<Equation> eSet3 = Stream.of(
			eck_enc, eik_enc, uk_dec, ck_dec, ik_dec, emD_enc, epD_enc, cerk_enc, merk_enc, amD_anon, stats_eval).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet3 = new LinkedHashSet<Trust>();
	// Compositions and Associations
	private static Composition MCiMAi = new Composition(MCi, MAi);
	private static Composition MCiMDik = new Composition(MCi, MDik);
	private static Composition CRSA = new Composition(CR, SA);
	private static Composition IDBiCR = new Composition(IDBi, CR); //TODO right now db-assoc = compos from DB to component?
	private static Composition RDBiCR = new Composition(RDBi, CR);
	private static Set<Composition> composSet3 = Stream.of(MCiMAi, MCiMDik, CRSA, IDBiCR, RDBiCR).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Set<architecture.Statement> stSet3 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasMA_ck = new Action(ActionType.HAS, MAi, cki);
	private static Action hasMA_ik = new Action(ActionType.HAS, MAi, iki);
	private static Action hasMA_puk = new Action(ActionType.HAS, MAi, pukik);
	private static Action hasP_pD = new Action(ActionType.HAS, Pj, pDj);
	private static Action hasMC_mD = new Action(ActionType.HAS, MCi, mDj);
	private static Action hasMC_euk = new Action(ActionType.HAS, MCi, eukik);
	private static Action hasMC_pubMk = new Action(ActionType.HAS, MCi, pubMk);
	private static Action hasMD_pwk = new Action(ActionType.HAS, MDik, pwkik);
	private static Action hasMD_rk = new Action(ActionType.HAS, MDik, rkj);
	private static Action hasCR_privMk = new Action(ActionType.HAS, CR, privMk);
	private static Action computeMA_eck = new Action(ActionType.COMPUTE, MAi, eck_enc);
	private static Action computeMA_eik = new Action(ActionType.COMPUTE, MAi, eik_enc);
	private static Action computeMD_uk = new Action(ActionType.COMPUTE, MDik, uk_dec);
	private static Action computeMD_ck = new Action(ActionType.COMPUTE, MDik, ck_dec);
	private static Action computeMD_ik = new Action(ActionType.COMPUTE, MDik, ik_dec);
	private static Action computeMD_epD = new Action(ActionType.COMPUTE, MDik, epD_enc);
	private static Action computeMD_emD = new Action(ActionType.COMPUTE, MDik, emD_enc);
	private static Action computeMD_cerk = new Action(ActionType.COMPUTE, MDik, cerk_enc);
	private static Action computeMD_merk = new Action(ActionType.COMPUTE, MDik, merk_enc);
	private static Action computeCR_amD = new Action(ActionType.COMPUTE, CR, amD_anon);
	private static Action computeR_stats = new Action(ActionType.COMPUTE, Rl, stats_eval);
	private static Action receiveMDMA = new Action(ActionType.RECEIVE, MDik, MAi, Collections.emptySet(), Set.of(eckik, eikik));
	private static Action receiveMCP = new Action(ActionType.RECEIVE, MCi, Pj, Collections.emptySet(), Set.of(pDj));
	private static Action receiveCRMD = new Action(ActionType.RECEIVE, CR, MDik, Collections.emptySet(), Set.of(emDj, merkj, cerkj, epDj));
	private static Action receiveRCR = new Action(ActionType.RECEIVE, Rl, CR, Collections.emptySet(), Set.of(amDj));
	private static Action receiveIDBCR = new Action(ActionType.RECEIVE, IDBi, CR, Collections.emptySet(), Set.of(epDj));
	private static Action receiveRDBCR = new Action(ActionType.RECEIVE, RDBi, CR, Collections.emptySet(), Set.of(cerkj, merkj, emDj));
	private static Action spotcheckMTC = new Action(ActionType.SPOTCHECK, M, MCi, Set.of(pDj, mDj));
	private static Action spotcheckMCR = new Action(ActionType.SPOTCHECK, M, CR, Set.of(epDj, emDj));
	private static Set<Action> aSet3 = Stream.of(
			hasMA_ck, hasMA_ik, hasMA_puk, hasP_pD, hasMC_mD, hasMC_euk, hasMC_pubMk, hasMD_pwk, hasMD_rk,
			hasCR_privMk, computeMA_eck, computeMA_eik, computeMD_uk, computeMD_ck, computeMD_ik, computeMD_epD, computeMD_emD, computeMD_cerk,
			computeMD_merk, computeCR_amD, computeR_stats, receiveMDMA, receiveMCP, receiveCRMD,
			receiveRCR, receiveIDBCR, receiveRDBCR, spotcheckMTC, spotcheckMCR).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	//TODO more and more practical values?
	private static Dep patient_dep1 = new Dep(cki, Set.of(eckik, ukik), 1);
	private static Dep patient_dep2 = new Dep(iki, Set.of(eikik, ukik), 1);
	private static Dep patient_dep3 = new Dep(ukik, Set.of(eukik, pwkik), 1);
	private static Dep patient_dep4 = new Dep(rkj, Set.of(merkj, privMk), 1);
	private static Dep patient_dep5 = new Dep(rkj, Set.of(cerkj, cki), 1);
	private static Dep patient_dep6 = new Dep(pukik, Collections.emptySet(), 1);
	private static Dep patient_dep7 = new Dep(pubMk, Collections.emptySet(), 1);
	private static Dep patient_dep8 = new Dep(privMk, Collections.emptySet(), 0.0001);
	private static Dep patient_dep9 = new Dep(cki, Collections.emptySet(), 0.0001);
	private static Dep patient_dep10 = new Dep(iki, Collections.emptySet(), 0.0001);
	private static Dep patient_dep11 = new Dep(pwkik, Collections.emptySet(), 0.0001);
	private static Dep patient_dep12 = new Dep(rkj, Collections.emptySet(), 0.0001);
	private static Dep patient_dep13 = new Dep(pDj, Set.of(epDj, iki), 1);
	private static Dep patient_dep14 = new Dep(mDj, Set.of(emDj, rkj), 1);
	private static Set<DependenceRelation> dSet3 = Stream.of(
			new DependenceRelation(MAi, patient_dep1), new DependenceRelation(MAi, patient_dep2),
			new DependenceRelation(MAi, patient_dep3), new DependenceRelation(MAi, patient_dep4),
			new DependenceRelation(MAi, patient_dep5), new DependenceRelation(MAi, patient_dep6),
			new DependenceRelation(MAi, patient_dep7), new DependenceRelation(MAi, patient_dep8),
			new DependenceRelation(MAi, patient_dep9), new DependenceRelation(MAi, patient_dep10),
			new DependenceRelation(MAi, patient_dep11), new DependenceRelation(MAi, patient_dep12),
			new DependenceRelation(MAi, patient_dep13), new DependenceRelation(MAi, patient_dep14),
			new DependenceRelation(MDik, patient_dep1), new DependenceRelation(MDik, patient_dep2),
			new DependenceRelation(MDik, patient_dep3), new DependenceRelation(MDik, patient_dep4),
			new DependenceRelation(MDik, patient_dep5), new DependenceRelation(MDik, patient_dep6),
			new DependenceRelation(MDik, patient_dep7), new DependenceRelation(MDik, patient_dep8),
			new DependenceRelation(MDik, patient_dep9), new DependenceRelation(MDik, patient_dep10),
			new DependenceRelation(MDik, patient_dep11), new DependenceRelation(MDik, patient_dep12),
			new DependenceRelation(MDik, patient_dep13), new DependenceRelation(MDik, patient_dep14),
			new DependenceRelation(MCi, patient_dep1), new DependenceRelation(MCi, patient_dep2),
			new DependenceRelation(MCi, patient_dep3), new DependenceRelation(MCi, patient_dep4),
			new DependenceRelation(MCi, patient_dep5), new DependenceRelation(MCi, patient_dep6),
			new DependenceRelation(MCi, patient_dep7), new DependenceRelation(MCi, patient_dep8),
			new DependenceRelation(MCi, patient_dep9), new DependenceRelation(MCi, patient_dep10),
			new DependenceRelation(MCi, patient_dep11), new DependenceRelation(MCi, patient_dep12),
			new DependenceRelation(MCi, patient_dep13), new DependenceRelation(MCi, patient_dep14),
			new DependenceRelation(SA, patient_dep1), new DependenceRelation(SA, patient_dep2),
			new DependenceRelation(SA, patient_dep3), new DependenceRelation(SA, patient_dep4),
			new DependenceRelation(SA, patient_dep5), new DependenceRelation(SA, patient_dep6),
			new DependenceRelation(SA, patient_dep7), new DependenceRelation(SA, patient_dep8),
			new DependenceRelation(SA, patient_dep9), new DependenceRelation(SA, patient_dep10),
			new DependenceRelation(SA, patient_dep11), new DependenceRelation(SA, patient_dep12),
			new DependenceRelation(SA, patient_dep13), new DependenceRelation(SA, patient_dep14),
			new DependenceRelation(CR, patient_dep1), new DependenceRelation(CR, patient_dep2),
			new DependenceRelation(CR, patient_dep3), new DependenceRelation(CR, patient_dep4),
			new DependenceRelation(CR, patient_dep5), new DependenceRelation(CR, patient_dep6),
			new DependenceRelation(CR, patient_dep7), new DependenceRelation(CR, patient_dep8),
			new DependenceRelation(CR, patient_dep9), new DependenceRelation(CR, patient_dep10),
			new DependenceRelation(CR, patient_dep11), new DependenceRelation(CR, patient_dep12),
			new DependenceRelation(CR, patient_dep13), new DependenceRelation(CR, patient_dep14),
			new DependenceRelation(Rl, patient_dep1), new DependenceRelation(Rl, patient_dep2),
			new DependenceRelation(Rl, patient_dep3), new DependenceRelation(Rl, patient_dep4),
			new DependenceRelation(Rl, patient_dep5), new DependenceRelation(Rl, patient_dep6),
			new DependenceRelation(Rl, patient_dep7), new DependenceRelation(Rl, patient_dep8),
			new DependenceRelation(Rl, patient_dep9), new DependenceRelation(Rl, patient_dep10),
			new DependenceRelation(Rl, patient_dep11), new DependenceRelation(Rl, patient_dep12),
			new DependenceRelation(Rl, patient_dep13), new DependenceRelation(Rl, patient_dep14)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static DeductionCapability dc_MAi = new DeductionCapability(MAi, Set.of(deduc4));
	private static DeductionCapability dc_MCi = new DeductionCapability(MCi, Set.of(deduc4));
	private static DeductionCapability dc_MDik = new DeductionCapability(MDik, Set.of(deduc4));
	private static DeductionCapability dc_SA = new DeductionCapability(SA, Set.of(deduc4));
	private static DeductionCapability dc_CR = new DeductionCapability(CR, Set.of(deduc4));
	private static DeductionCapability dc_Rl = new DeductionCapability(Rl, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet3 = Stream.of(dc_MAi, dc_MCi, dc_MDik, dc_SA, dc_CR, dc_Rl).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property patient_prop1 = new Property(PropertyType.HAS, Rl, 0.001, pDj);
	private static Property patient_prop2 = new Property(PropertyType.NEGATION, patient_prop1);
	private static Property patient_prop3 = new Property(PropertyType.HAS, CR, 0.001, pDj);
	private static Property patient_prop4 = new Property(PropertyType.NEGATION, patient_prop3);
	private static Property patient_prop5 = new Property(PropertyType.HAS, SA, 0.001, pDj);
	private static Property patient_prop6 = new Property(PropertyType.NEGATION, patient_prop5);
	private static Property patient_prop7 = new Property(PropertyType.CONJUNCTION, patient_prop2, patient_prop4);
	private static Property patient_prop8 = new Property(PropertyType.CONJUNCTION, patient_prop6, patient_prop7);
	private static Property patient_prop9 = new Property(PropertyType.HAS, Rl, 0.001, mDj);
	private static Property patient_prop10 = new Property(PropertyType.NEGATION, patient_prop9);
	//TODO more?
	private static Set<Property> pSet3 = Stream.of(patient_prop2, patient_prop4, patient_prop6, patient_prop8, patient_prop10).collect(Collectors.toCollection(LinkedHashSet::new));


	/**
	 * Method to load one of the case studies.
	 * 
	 * @param archFunc
	 * 			the architecture functions object that holds all information about the architecture
	 * @param example
	 * 			the case study to load
	 */
	public static void load(ArchitectureFunctions archFunc, CaseStudy example) {
		// set all the necessary list of the right architecture
		switch (example) {
		case SEM:
			// first case study: smart energy metering
			archFunc.setcSet(cSet1);
			archFunc.setvSet(vSet1);
			archFunc.settSet(tSet1);
			archFunc.seteSet(eSet1);
			archFunc.settrustSet(trustSet1);
			archFunc.setstSet(stSet1);
			archFunc.setaSet(aSet1);
			archFunc.setdSet(dSet1);
			archFunc.setdedSet(dedSet1);
			archFunc.setpSet(pSet1);
			archFunc.setcomposSet(composSet1);
			break;
		case AW:
			// second case study: accuweather ios app
			archFunc.setcSet(cSet2);
			archFunc.setvSet(vSet2);
			archFunc.settSet(tSet2);
			archFunc.seteSet(eSet2);
			archFunc.settrustSet(trustSet2);
			archFunc.setstSet(stSet2);
			archFunc.setaSet(aSet2);
			archFunc.setdSet(dSet2);
			archFunc.setdedSet(dedSet2);
			archFunc.setpSet(pSet2);
			archFunc.setcomposSet(composSet2);
			break;
		case PDR:
			// third case study: patient data register
			archFunc.setcSet(cSet3);
			archFunc.setvSet(vSet3);
			archFunc.settSet(tSet3);
			archFunc.seteSet(eSet3);
			archFunc.settrustSet(trustSet3);
			archFunc.setstSet(stSet3);
			archFunc.setaSet(aSet3);
			archFunc.setdSet(dSet3);
			archFunc.setdedSet(dedSet3);
			archFunc.setpSet(pSet3);
			archFunc.setcomposSet(composSet3);
			break;
		default:
			break;
		}
	}

}
