<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>Prescription</sch:title>
    <sch:rule context="/f:Prescription/f:identifier/f:assigner">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:patient">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:prescriber">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:dispense/f:quantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:dispense/f:dispenser">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:activeIngredient/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:activeIngredient/f:quantityRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:activeIngredient/f:quantityRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:inactiveIngredient/f:identification">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:inactiveIngredient/f:quantityRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:medicine/f:inactiveIngredient/f:quantityRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest">
      <sch:assert test="(exists(f:duration) and not (exists(f:start) or exists(f:end))) or (exists(f:start) and exists(f:duration)) != (exists(f:start))">Restrictions on the combinations of Duration, Start and End are as follows: Duration may be specified alone or with Start.  Alternatively, Start may be specified if End is specified.  No other combinations are allowed.</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:totalPeriodicDosis/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:totalPeriodicDosis/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:duration">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:precondition">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:additionalInstruction">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:route">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange">
      <sch:assert test="not(exists(f:low/f:range) or exists(f:high/f:range))">Quantity values cannot have a range when used in a Range</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange/f:low">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:doseRange/f:high">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:rate">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule">
      <sch:assert test="not(exists(f:repeat)) or count(f:event)&lt;2">There can only be a repeat element if there is none or one event</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule/f:repeat">
      <sch:assert test="not(exists(f:count) and exists(f:end))">At most, only one of count and end can be present</sch:assert>
      <sch:assert test="exists(f:frequency) != exists(f:when)">Either frequency or when must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:administrationRequest/f:dosageInstruction/f:schedule/f:repeat/f:duration">
      <sch:assert test=". &gt; 0">duration must be a positive value</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:reason">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Prescription/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
