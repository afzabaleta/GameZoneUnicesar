# GRASP Analysis - Requirement 7

Answers to the five guiding questions of the GRASP audit and correction.
The audit is in `docs/grasp-audit/`. The correction log and the
non-regression evidence are in `docs/grasp-refactor-log.md`.

Authors:

- Questions 1 and 3: Sherly Michell Corrales Maestre (Developer 1).
- Questions 2 and 5: Andres Felipe Zabaleta Diaz (Technical Lead).
- Question 4: Diego Armando Mestre Gomez (Developer 2); revised by the
  Technical Lead during the consolidation.

---

## 1. Verdict criterion and minimum evidence

*What criterion did the team use to decide between violation, correct and
not applicable? What minimum evidence must support a verdict?*

For each pattern we asked the question that defines it (who has the
information, who should create, who receives the event, and so on) about
the concrete classes in our scope.

- **Violation:** the code gives a responsibility to a class that the
  pattern says should not have it, **and** we can name a concrete problem
  it causes today or under a foreseeable change (new product type, new sale
  identifier, new message). If we could not describe a consequence, it was
  not registered as a violation.
- **Correct:** the pattern has an application point in our code and the
  code follows it. We had to point to the class and method where it is
  applied well.
- **Not applicable:** the pattern has no application point in the audited
  classes. For example, a class that receives no system events cannot
  violate Controller. We had to explain why there is no application point.

We also agreed on **accepted trade-offs**. When following one pattern would
break a more important rule of the project, the code is not a violation,
but the report explains the decision. Example: `instanceof` inside the
`toCsv` methods of the repositories, because moving file-column mapping
into the model would break the layer rules.

**Minimum evidence for a violation:** class, method and line numbers at the
audited commit (`81ea350`), the fragment or a precise description of it,
why it breaks the pattern, the consequence, the proposed fix with the
pattern applied, and whether another member's code is involved. A verdict
of "correct" needs at least one class and method where the pattern is
applied. A verdict of "not applicable" needs the reason.

## 3. Fragments that break more than one pattern

*Did the team find fragments that break more than one pattern at the same
time? How were they registered without duplicating findings, and how was
the guiding pattern chosen?*

Yes. The clearest case is `CategoryDiscount.calculateDiscount`. Its
`instanceof VideoGame/Console/Accessory` chain is a conditional on type
(Polymorphism), and it also leaves the "kinds of product" variation point
unprotected (Protected Variations). Other examples:

- `ReturnService.calculateMonthlySales`: Information Expert and High
  Cohesion.
- `AccessoryService.validateAccessory`: Pure Fabrication and High Cohesion.

**Rule to avoid duplicates:** each fragment is registered **once**, under
the pattern that guides its fix. The section of the other pattern gives a
verdict and references the ID ("Violation - see V-02"). The consolidated
table shows those as *referenced*.

**How we chose the guiding pattern:** we asked "which pattern tells us what
the new design is?"

- For `CategoryDiscount`, the fix is to ask the product polymorphically
  (`Product.getCategory()`), so Polymorphism guides it. Protected
  Variations is the benefit, not the mechanism.
- For the monthly sales total, the fix is to move the calculation to
  whoever has the sales, so Information Expert guides it.
- For the accessory validation, the fix is a new fabricated validator, so
  Pure Fabrication guides it.


## 4. Preserving external behaviour

*The correction must preserve the external behaviour of the system. What
strategy does the team follow to guarantee that every menu operation still
works the same? What evidence is shown at the end?*

The strategy is a **black-box comparison of the console output and of the
data files, before and after** the corrections.

1. **Fixed data set.** Each run starts from a copy of the `data/` folder of
   `develop`, so every run begins in the same state.
2. **Scripted input.** The 31 menu operations (Products 3, Accessories 6,
   People 3, Sales 4, Promotions 6, Returns 5, Warranties 4) are covered by
   input scripts stored in `docs/regression/`. They include valid data and,
   for the operations that validate input, invalid cases (blank identifier,
   non-numeric price, unknown customer, wrong date format, invalid category,
   and so on). The program is run with the script as standard input.
3. **What we compare.** The console output and the content of every file in
   `data/` after the run, against the baseline taken on `develop` before the
   first correction.
4. **Values that change on every run.** The return identifier
   `RET-<milliseconds>`, the warranty identifiers `WAR-...-<uuid>` and the
   object reference printed in the return receipt are replaced with fixed
   placeholders before comparing. The baseline and the final run are made
   on the same day, so the sale dates match.
5. **When it runs.** Once on `develop` before the first correction
   (reference result), in each Phase 2 Pull Request on the operations its
   violations affect, and at the end over all 31 operations.

**Evidence:** `docs/grasp-refactor-log.md` records the strategy, the list of
scripts, the result of the initial run, the result of the final run (no
differences in any operation) and the result of `mvn clean compile`. The
same scripts are run live in the oral defence.