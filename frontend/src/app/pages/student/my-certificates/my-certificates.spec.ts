import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyCertificates } from './my-certificates';

describe('MyCertificates', () => {
  let component: MyCertificates;
  let fixture: ComponentFixture<MyCertificates>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyCertificates],
    }).compileComponents();

    fixture = TestBed.createComponent(MyCertificates);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
