<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>LabReport</sch:title>
    <sch:rule context="/f:LabReport/f:patient">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:admission">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:laboratory">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:requestDetail/f:requestTest">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:requestDetail/f:requester">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:requestDetail/f:clinicalInfo">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:reportName">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:service">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:specimen">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:name">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:specimen">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:name">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:valueQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:valueCodeableConcept">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:valueRatio/f:numerator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:valueRatio/f:denominator">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:meaning">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeQuantity">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange">
      <sch:assert test="not(exists(f:low/f:range) or exists(f:high/f:range))">Quantity values cannot have a range when used in a Range</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange/f:low">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:resultGroup/f:result/f:referenceRange/f:rangeRange/f:high">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:codedDiagnosis">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:LabReport/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
