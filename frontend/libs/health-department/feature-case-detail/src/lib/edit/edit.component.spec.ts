import { IndexCaseService } from '@qro/health-department/domain';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditComponent } from './edit.component';
import { MatDialog } from '@angular/material/dialog';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { SnackbarService } from '@qro/shared/util-snackbar';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ValidationErrorService } from '@qro/shared/util-forms';

describe('EditComponent', () => {
  let component: EditComponent;
  let fixture: ComponentFixture<EditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule],
      declarations: [EditComponent],
      providers: [
        { provide: MatDialog, useValue: {} },
        { provide: SnackbarService, useValue: {} },
        { provide: IndexCaseService, useValue: {} },
        { provide: ActivatedRoute, useValue: {} },
        { provide: ValidationErrorService, useValue: { getErrorKeys: () => [] } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
