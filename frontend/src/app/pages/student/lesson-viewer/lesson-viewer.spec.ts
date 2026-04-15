import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonViewer } from './lesson-viewer';

describe('LessonViewer', () => {
  let component: LessonViewer;
  let fixture: ComponentFixture<LessonViewer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonViewer],
    }).compileComponents();

    fixture = TestBed.createComponent(LessonViewer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
