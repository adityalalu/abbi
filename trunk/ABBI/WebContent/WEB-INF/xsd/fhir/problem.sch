<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:ns prefix="f" uri="http://hl7.org/fhir"/>
  <sch:pattern>
    <sch:title>Problem</sch:title>
    <sch:rule context="/f:Problem/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:category">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:certainty">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:severity">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:onsetAge">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:abatementAge">
      <sch:assert test="not(exists(f:code)) or exists(f:system)">If a code for the units is present, the system must also be present</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:stage">
      <sch:assert test="exists(f:summary) or exists(f:assessment)">Stage must have summary or assessment</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:stage/f:summary">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:stage/f:assessment">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:evidence">
      <sch:assert test="exists(f:code) or exists(f:details)">evidence must have code or details</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:evidence/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:evidence/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:location">
      <sch:assert test="exists(f:code) or exists(f:details)">location must have code or details</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:location/f:code">
      <sch:assert test="not(exists(f:primary)) or count(f:coding[@id=current()/f:primary])=1">If a primary reference is present, it must point to one of the codings</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:location/f:details">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:relatedItem/f:target">
      <sch:assert test="not(contains(f:version, ':')) or exists(f:id)">If the version url is provided, and local, then a logical id must be provided too</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
    <sch:rule context="/f:Problem/f:extension//f:extension">
      <sch:assert test="not(exists(f:extension/f:ref))">Nexted Extensions cannot have references</sch:assert>
      <sch:assert test="exists(f:*[starts-with(local-name(.), 'value')]) != exists(f:extension)">Either a value or nested extensions must be present, but not both</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
