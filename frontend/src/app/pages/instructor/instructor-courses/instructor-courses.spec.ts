import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorCourses } from './instructor-courses';

describe('InstructorCourses', () => {
  let component: InstructorCourses;
  let fixture: ComponentFixture<InstructorCourses>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorCourses],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorCourses);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
