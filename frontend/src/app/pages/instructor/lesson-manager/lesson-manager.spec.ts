import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonManager } from './lesson-manager';

describe('LessonManager', () => {
  let component: LessonManager;
  let fixture: ComponentFixture<LessonManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonManager],
    }).compileComponents();

    fixture = TestBed.createComponent(LessonManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
