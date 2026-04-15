import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorOverview } from './instructor-overview';

describe('InstructorOverview', () => {
  let component: InstructorOverview;
  let fixture: ComponentFixture<InstructorOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorOverview],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorOverview);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
