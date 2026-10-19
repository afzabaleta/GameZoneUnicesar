# GRASP Analysis - Requirement 7

Answers to the five guiding questions of the GRASP audit and correction.
The audit is in `docs/grasp-audit/`. The correction log and the
non-regression evidence are in `docs/grasp-refactor-log.md`.

Authors:

- Questions 1 and 3: Sherly Michell Corrales Maestre (Developer 1).
- Questions 2 and 5: Andres Felipe Zabaleta Diaz (Technical Lead).
- Question 4: Diego Armando Mestre Gomez (Developer 2).

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
- ## 4. Violations found in Developer 2's scope

The audit of Developer 2's scope identified violations involving Information Expert, Low Coupling, Polymorphism, Pure Fabrication, Indirection and Protected Variations.

- **Information Expert:** `ReturnService` calculates monthly sales totals, although the calculation should belong to the class that owns the relevant sales information. The finding is registered as V-02.
- **Low Coupling:** `ReturnRepository` depends on service classes to locate sales and items. Persistence should remain independent of business services. The finding is registered as V-04.
- **Polymorphism:** `ReturnService` uses `instanceof` checks to distinguish products from accessories and to identify consoles for warranty cancellation. The design should delegate these decisions to polymorphic behavior or a shared abstraction. The findings are registered as V-05 and V-06.
- **Pure Fabrication:** accessory validation and inventory coordination require supporting responsibilities to be separated from the services that currently perform them. See V-08 and V-09 in the audit report.
- **Indirection:** return operations should use `InventoryService` as an intermediary for item lookup and stock restoration, rather than depending directly on separate product and accessory services. See V-10.
- **Protected Variations:** the return flow should depend on stable abstractions and polymorphic operations so that adding new item types does not require changing the same conditional logic in several classes. See V-05, V-07 and V-11.

The audit also reviewed Creator and Controller. Their verdicts and evidence are documented in `docs/grasp-audit/developer2-audit.md`.

These findings are documented as recommendations for the correction phase. No Java implementation changes are included in this audit.